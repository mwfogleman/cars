(ns cars.core
  (:require [clj-http.client :as client]
            [clojure.string :as str]
            [com.rpl.specter :as S])
  (:gen-class))

(def base-url "http://www.nhtsa.gov/webapi/api/SafetyRatings")
(def format-string "?format=json")

(def joiner (partial str/join "/"))

(defn getter
  [url]
  (client/get url {:as :json}))

(S/declarepath REQ-BODY)
(S/providepath REQ-BODY [:body :Results S/FIRST])

(defn year-slug
  [year]
  (joiner ["modelyear" year]))

(defn make-slug
  [make]
  (joiner ["make" make]))

(defn model-slug
  [model]
  (joiner ["model" model]))

(defn vehicle-slug
  [id]
  (joiner ["vehicleid" id]))

(defn combine-slugs
  [& slugs]
  (->> [base-url slugs format-string]
       flatten
       joiner))

(defn valid-request?
  [url]
  (let [request (getter url)]
    (> (S/select-any [:body :Count] request) 0)))

(defn get-car-vehicle-id
  [year make model]
  (let [url     (combine-slugs (year-slug year) (make-slug make) (model-slug model))
        request (getter url)]
    (S/select-any [REQ-BODY :VehicleId] request)))

(defn get-vehicle-safety-results
  [id]
  (let [url     (combine-slugs (vehicle-slug id))
        request (getter url)]
    (S/select-any [REQ-BODY] request)))

(defn get-car-safety-results
  [year make model]
  (->> (get-car-vehicle-id year make model)
       get-vehicle-safety-results))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
