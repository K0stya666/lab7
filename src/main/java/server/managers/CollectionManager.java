package server.managers;

import global.models.Route;
import org.slf4j.*;
import server.managers.databases.Interstate60;
import server.utility.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Менеджер коллекции
 * @author Kostya666
 */
public class CollectionManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(CollectionManager.class);
    private final Map<Integer, Route> routes = new HashMap<>();
    private final Map<Route, Integer> userIdMap = new HashMap<>();
    private final List<Route> collection = new LinkedList<>();
    private final Interstate60 interstate60;
    private final ReentrantLock lock = new ReentrantLock();
    private Date lastSaveTime;

    public CollectionManager() {
        this.lastSaveTime = null;
        this.interstate60 = new Interstate60();
        this.loadCollection();
        update();
    }
//    public CollectionManager(Interstate60 interstate60) {
//        this.interstate60 = interstate60;
//        this.loadCollection();
//        update();
//    }
        public CollectionManager(Interstate60 interstate60) {
            this.interstate60 = interstate60;
            this.loadCollection();
            update();
        }

    public Date getLastSaveTime() { return lastSaveTime; }

    /**
     * @return коллекция
     */
    public List<Route> getCollection() {
        return collection;
    }

    /**
     * @param id id Route
     * @return Route по id
     */
    public Route byId(Integer id) {
        try {
            lock.lock();
            return routes.get(id);
        } finally {
            lock.unlock();
        }
    }

    /**
     *
     * @param id
     */
    public void removeById(Integer id) {
        try {
            lock.lock();
            routes.remove(id);
            collection.remove(byId(id));
            interstate60.
        }
    }

//    public Integer add(Route route, int userId) {
//        try {
//            lock.lock();
//            int newId = interstate60.addRoute(route, userId);
//            if (newId < 0) return -1;
//            route.setId(newId);
//            routes.put(newId, route);
//            collection.add(route);
//            update();
//            return newId;
//        } finally {
//            lock.unlock();
//        }
//    }

    /**
     * Добавляет Route
     * @param route маршрут
     * @param user пользователь
     */
    public void add(Route route, User user) {
        try {
            lock.lock();
            interstate60.addRoute(route, user);
            collection.add(route);
            routes.put(route.getId(), route);
            userIdMap.put(route, user.getId());
            update();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Обновляет маршрут
     * @param route маршрут
     * @return успешность обновления
     */
    public boolean update(Route route) {
        try {
            lock.lock();
            if (!contains(route)) { return false; }
            //if (!interstate60.removeRouteById(route.getId())) return false;
            collection.remove(route);
            routes.remove(route.getId());
            collection.add(route);
            update();
            return true;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Выясняет содержание маршрута в коллекции
     * @param route проверяемый маршрут
     * @return содержание маршрута
     */
    public boolean contains(Route route) {
        try {
            lock.lock();
            for (Route r : collection) {
                if (Objects.equals(r.getId(), route.getId())) {
                    return true;
                }
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Удаляет Route по id
     */
    public void remove(Integer id) {
        try {
            lock.lock();
            var route = byId(id);
            if (route == null) return;
            routes.remove(route.getId());
            collection.remove(route);
            update();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Сортирует коллекцию
     */
    public void update() {
        Collections.sort(collection);
    }

    public void clear() {
        try {
            lock.lock();
            collection.clear();
            routes.clear();
            interstate60.clearRoutes();
        } finally {
            lock.unlock();
        }
    }

    public boolean loadCollection() {
        try {
            lock.lock();
            routes.clear();
            Collection<Route> loadedCollection = interstate60.getRoutes();
            collection.addAll(loadedCollection);
            LOGGER.info("Routes added successfully.");
            validateAll();
            return true;
        } finally {
            lock.unlock();
        }
    }

    public void validateAll() {
        AtomicBoolean flag = new AtomicBoolean(true);
        collection.forEach(route -> {
            if (!route.validate()) {
                flag.set(false);
                LOGGER.error("Маршрут с id={} содержит недопустимые поля", route.getId());
            }
        });
        if (flag.get()) {
            LOGGER.info("Загруженные маршруты валидны");
        }
    }

    @Override
    public String toString() {
        if (collection.isEmpty()) return "Коллекция пуста";
        StringBuilder info = new StringBuilder();
        for (var route : collection) {
            info.append(route).append("\n");
        }
        return info.toString().trim();
    }
}