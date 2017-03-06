(ns swarmpit.controller
  (:require [bidi.router :as br]
            [ajax.core :refer [GET POST]]
            [swarmpit.component.service.create :as screate]
            [swarmpit.component.service.info :as sinfo]
            [swarmpit.component.service.list :as slist]))

(defmulti dispatch (fn [location] (:handler location)))

(def location (atom nil))

(def handler ["" {"/"         :index
                  "/services" {""        :service-list
                               "/create" :service-create
                               ["/" :id] :service-info}}])

(defn start
  []
  (let [router (br/start-router! handler
                                 {:on-navigate
                                  (fn [loc]
                                    (dispatch loc)
                                    (reset! location loc))})
        route (:handler @location)]
    (if (some? route)
      (br/set-location! router @location))))

(defmethod dispatch :index
  [_]
  (print "index"))

(defmethod dispatch :service-list
  [_]
  (print "list")
  (GET "/services"
       {:handler (fn [response]
                   (slist/mount! response))}))

(defmethod dispatch :service-info
  [{:keys [route-params]}]
  (print "info")
  (GET (str "/services/" (:id route-params))
       {:handler (fn [response]
                   (sinfo/mount! response))}))

(defmethod dispatch :service-create
  [_]
  (print "create")
  (screate/mount!))

(defmethod dispatch nil
  [_]
  (print "not-found"))




