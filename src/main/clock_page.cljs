(ns clock-page
  (:require
   [reagent.core :as reagent]
   [date-fns :as date-fns]))

(defn clock []
  (let [time-now (reagent/atom (.now js/Date))
        state (reagent/atom {})
        ]
    (reagent/create-class
     {:display-name "clock component"
      :component-did-mount (fn [] (js/setInterval #(reset! time-now (.now js/Date)) 500))  ; every 500ms (1/2 second) to be more accurate.
      :component-did-update (fn [] (println "clock - component-did-update"))
      :component-will-unmount (fn [] (println "clock - component-will-unmount"))

      :reagent-render (fn [] [:div#clock 
                              [:div "Date: " (date-fns/format @time-now "MM/dd/yyyy")]
                              [:div "Time: " (date-fns/format @time-now "h m s aaa")]
                              ])}))
  )

(defn counting-button-in-form3 [txt]
  (let [state (reagent/atom 0)]
    (reagent/create-class
     {:reagent-render  ; This one is REQUIRED. Only one that is required.
      (fn [txt]
        [:button
         {:class "bg-white hover:bg-gray-100 text-gray-800 font-semibold py-2 px-4 border border-gray-400 rounded shadow"
          :on-click (fn [e]
                      (swap! state (fn [a] (+ a 2))))}
         (str txt " - " @state)])})))

(defn clock-panel []
  [:div
   [:h1 "clock panel"]
   [clock]
  ;  [counting-button-in-form3]

  ;  [:br]
  ;  (date-fns/format (.getTime (js/Date.)) "MM/dd/yyyy")
  ;  [:br]
  ;  (date-fns/format (.now js/Date) "MM/dd/yyyy")
   ])

(defn clock-page-container []
  [:div
   [:h1 "clock Container"]
   [clock-panel]]
  )
