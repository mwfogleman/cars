(ns cars.core
  (:require [cheshire.core :refer :all]
            [clj-http.client :as client]
            [clojure.string :as str])
  (:gen-class))

;; get model year
;; get make
;; get model
;; get vehicle id from api
;; get safety ratings from vehicle id through api

(def base-url "http://www.nhtsa.gov/webapi/api/SafetyRatings")
(def year "/modelyear/2005")
(def make "/make/honda")
(def model "/model/insight")
(def format-string "/?format=json")
(def car-url (str/join [base-url year make model format-string])) ;;  "http://www.nhtsa.gov/webapi/api/SafetyRatings/modelyear/2005/make/honda/model/insight/?format=json"

(def car-result (client/get car-url))

(keys car-result) ;; (:request-time :repeatable? :protocol-version :streaming? :chunked? :reason-phrase :headers :orig-content-encoding :status :length :body :trace-redirects)

(def id (-> car-result
            (get :body)
            parse-string
            (get "Results")
            first
            (get "VehicleId")))

(def safety-url (str/join [base-url "/vehicleid/" id "/" format-string]))

(def safety-result (client/get safety-url))

(def safety-parsed (into (sorted-map) (-> safety-result
                                          (get :body)
                                          parse-string
                                          (get "Results")
                                          first)))

;; from urllib2 import urlopen
;; from json import load

;; apiUrl = "http://www.nhtsa.gov/webapi/api/SafetyRatings"
;; apiParam = "/vehicleid/10860/"
;; outputFormat = "?format=json"

;; #Combine all three variables to make up the complete request URL
;; response = urlopen(apiUrl + apiParam + outputFormat)

;; #code below is only to handle JSON response object/format
;; #use equivalent sets of commands to handle xml response object/format
;; json_obj = load(response)

;; #Load the Result (vehicle collection) from the JSON response
;; print '------------------------------'
;; print '           Result			 '
;; print '------------------------------'
;; for objectCollection in json_obj['Results']:
;; # Loop each vehicle in the vehicles collection
;; for safetyRatingAttribute, safetyRatingValue in objectCollection.iteritems():
;; print safetyRatingAttribute, ": ", safetyRatingValue

;; # After running this example, feel free to explore the results below

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
