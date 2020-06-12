(ns util.dev
  (:require
   [reagent.core :as reagent]))


; Temp
; Better way to get iteration number. Maybe merge in at start?
(defn dev-panel
  "Vector of atoms (state)"
  [states]
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