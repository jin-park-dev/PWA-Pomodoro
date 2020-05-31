(ns util.dev
  (:require
   [reagent.core :as reagent]
   [stylefy.core :as stylefy :refer [use-style]]
   [date-fns :as date-fns]
   [util.time :refer [seconds->duration]]))


; Temp
; Better way to get iteration number. Maybe merge in at start?
(defn dev-panel [states]
  "Vector of atoms (state)"
  [:div.bg-teal-200.shadow.p-4.mt-5
   [:h1.text-4xl.font-bold.mb-2 "Dev-panel"]
   #_[:div
      [:h2.text-3xl.mb-2 "state"]
      [:div
       (pr-str @states)]]
   (doall
    (for [state states
          :let [counter (atom 0)]]
      [:div.mt-4 {:key (gensym)}
       [:h2.text-3xl.mb-2 "state"]
       [:div
        (pr-str @state)]]))])