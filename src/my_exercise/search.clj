(ns my-exercise.search
  (:require [hiccup.page :refer [html5]]
            [ring.util.response :as response]
            [clojure.string :as cstring] 
            [clojure.edn :as edn]
            [clj-http.client :as client]
            [my-exercise.us-state :as us-state]))

; TODO: header copied from home. the same except for title, so if I had time, I would
; make a function that takes a title and prints a header like this
(defn header [_]
  [:head
   [:meta {:charset "UTF-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1.0, maximum-scale=1.0"}]
   [:title "Upcoming Elections"]
   [:link {:rel "stylesheet" :href "default.css"}]])

; TODO: these helpers for constructing OCD-IDs probably could go into its own namespace
(def ocd-id-base "ocd-division/country:us")

(defn state-ocd-id [state]
  "return the ocd-id for a given state"
  (str ocd-id-base "/state" ":" (cstring/lower-case state)))

(defn city-ocd-id [state-ocd-id city]
  "return the ocd-id for a given city"
  (str state-ocd-id "/place" ":" 
       (cstring/replace (cstring/lower-case city) " " "_")))

(defn get-ocd-ids [ {{state :state city :city} :params} ]
  "given the form request, get back a list of ocd-ids"
  (let [state-ocd-id (state-ocd-id state) 
        city-ocd-id (city-ocd-id state-ocd-id city) ] 
    (list state-ocd-id city-ocd-id)))

; TODO: place all api calls into its own namespace
(def api-url "https://api.turbovote.org/")
(def api-upcoming-elections (str api-url "elections/upcoming"))

(defn get-upcoming-elections [ocd-ids] 
  "get the upcoming elections response from api for a list of ocd-ids as a list"
  (edn/read-string 
    (:body (client/get api-upcoming-elections 
                       {:query-params {"district-divisions" (cstring/join "," ocd-ids)}}))))

; There is a lot of information in the map TurboVote returns, I'll be printing out
; just the description and the date
(defn get-election-summary [{description :description date :date}]
  "take a election map as returned by the TurboVote API and return a summary string"
  (str description " (" date ")"))

; this function will construct a list of relevant ocd-ids from the request,
; and use it to make an API call to TurboVotes API to find the upcoming elections
; it will iterate over the list and print out a summary for each election
(defn print-upcoming-elections [request]
   [:div {:class "upcoming-elections"} 
    [:h1 "Upcoming Elections"] 
    [:ul
      (for [election (get-upcoming-elections (get-ocd-ids request))]
        ; there is a lot to print from the response we get back from the API
        ; but given the time constraints, I'll just be printing a summary
        [:li (get-election-summary election)])]])

;validate that we recieved the city and state params
(defn valid [request]
  (and request
       (:params request) 
       (not-empty (:state (:params request))) 
       (not-empty (:city (:params request)))))

; TODO: some rudimentary validation here, would have liked to have done more
(defn page [request]
  (if (valid request)
    (html5 
      (header request)
      (print-upcoming-elections request))
    (response/not-found "missing parameters")))
       
