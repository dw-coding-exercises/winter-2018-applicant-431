(ns my-exercise.core
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.params :refer [wrap-params]] 
            [my-exercise.home :as home]
            [my-exercise.search :as search]))

(defroutes app
  (GET "/" [] home/page)
  (POST "/search" [request] search/page) 
  (route/resources "/")
  (route/not-found "Not found"))

(def handler
  (-> app
      (wrap-defaults site-defaults)
      (wrap-params)
      wrap-reload))
