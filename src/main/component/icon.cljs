(ns component.icon
  (:require [reagent.core :as reagent]))


(defn play
  "Display only play icon"
  []
  [:i.las.la-play])

(defn stop
  "Display only stop icon"
  []
  [:i.las.la-stop])

(defn pause
  "Display only stop icon"
  []
  [:i.las.la-pause])
