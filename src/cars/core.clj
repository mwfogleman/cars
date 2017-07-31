(ns cars.core
  (:require [clj-http.client :as client]
            [clojure.string :as str]
            [com.rpl.specter :as S])
  (:gen-class))

(def min-year 1990)
(def current-year (->> (new java.util.Date) (.format (java.text.SimpleDateFormat. "yyyy")) Long.))
(def max-year (inc current-year))

(def base-url "http://www.nhtsa.gov/webapi/api/SafetyRatings")
(def format-string "?format=json")

(def joiner (partial str/join "/"))

(defn getter
  [url]
  (client/get url {:as :json}))

(S/declarepath REQ-BODY)
(S/providepath REQ-BODY [:body :Results S/FIRST])

(defn in?
  "True if the collection contains the element."
  [collection element]
  (some #(= element %) collection))

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

(defn get-makes
  [year]
  (let [url     (combine-slugs (year-slug year))
        request (getter url)]
    (S/select [:body :Results S/ALL :Make] request)))

(defn get-models
  [year make]
  (let [url     (combine-slugs (year-slug year) (make-slug make))
        request (getter url)]
    (S/select [:body :Results S/ALL :Model] request)))

(defn valid-year?
  [year]
  (>= max-year year min-year))

(defn valid-make?
  [year make]
  {:pre [(valid-year? year)]}
  (let [results (get-makes year)]
    (in? (map str/lower-case results) (str/lower-case make))))

(defn valid-model?
  [year make model]
  {:pre [(valid-make? year make)]}
  (let [results (get-models year make)]
    (in? (map str/lower-case results) (str/lower-case model))))

(defn valid-request?
  [url]
  (let [request      (getter url)
        result-count (S/select-any [:body :Count] request)]
    (> result-count 0)))

(defn valid-id?
  [id]
  (valid-request? (combine-slugs (vehicle-slug id))))

(defn get-car-vehicle-id
  [year make model]
  {:pre [(valid-year? year)
         (valid-make? year make)
         (valid-model? year make model)]}
  (let [url     (combine-slugs (year-slug year) (make-slug make) (model-slug model))
        request (getter url)]
    (S/select-any [REQ-BODY :VehicleId] request)))

(defn get-vehicle-safety-results
  [id]
  {:pre [(valid-id? id)]}
  (let [url     (combine-slugs (vehicle-slug id))
        request (getter url)]
    (S/select-any [REQ-BODY] request)))

(defn get-car-safety-results
  [year make model]
  {:pre [(valid-year? year)
         (valid-make? year make)
         (valid-model? year make model)]}
  (->> (get-car-vehicle-id year make model)
       get-vehicle-safety-results))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
