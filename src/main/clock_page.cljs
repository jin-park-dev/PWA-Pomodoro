(ns clock-page
  (:require
   [reagent.core :as reagent]
   [date-fns :as date-fns]))

; Turns out reagent has it's own way of doing it regarding issue with assigning variable and destruction that's not done JS way (and form3 isn't requried)
; Replaces, "create-class :component-did-mount / :component-will-unmount pattern" - https://www.reddit.com/r/Clojurescript/comments/5htkbc/how_to_use_the_withlet_macro_in_reagent_060/
#_(defn clock-form3 []
  (reagent/with-let [time-now (reagent/atom (.now js/Date))
                     state (reagent/atom {})]
    (reagent/create-class
     {:display-name "clock component"
      :component-did-mount (fn [] (js/setInterval #(reset! time-now (.now js/Date)) 500))  ; every 500ms (1/2 second) to be more accurate.
      :component-did-update (fn [] (println "clock - component-did-update"))
      :component-will-unmount (fn [] (js/clearInterval time-now))

      :reagent-render (fn [] [:div#clock
                              {:on-click (fn [] (js/clearInterval time-now))}
                              [:div "Date: " (date-fns/format @time-now "MM/dd/yyyy")]
                              [:div "Time: " (date-fns/format @time-now "h:mm:ss aaa")]])})))

; with-let seems only need form-1 or is it form-2 ??? - https://github.com/reagent-project/reagent/issues/378
; Found with-let here - https://stackoverflow.com/questions/30280484/making-a-simple-countdown-timer-with-clojure-reagent
(defn clock-simple []
  (reagent/with-let [time-now (reagent/atom (.now js/Date))
                     timer-fn (js/setInterval #(reset! time-now (.now js/Date)) 500)
                     state (reagent/atom {:currently-unused ""})]

    [:div#clock-simple
     {:on-click (fn [] (println "clicked clock-simple"))}
     [:div "Date: " (date-fns/format @time-now "MM/dd/yyyy")]
     [:div "Time: " (date-fns/format @time-now "h:mm:ss aaa")]]
                    
    (finally (js/clearInterval timer-fn))))

(defn clock-styled []
  (reagent/with-let [time-now (reagent/atom (.now js/Date))
                     timer-fn (js/setInterval #(reset! time-now (.now js/Date)) 500)
                     state (reagent/atom {:currently-unused ""})]

    [:div#clock-styled.flex.flex-row
     [:div (date-fns/format @time-now "h")]
     [:div ":"]
     [:div (date-fns/format @time-now "mm")]
     [:div ":"]
     [:div.mr-1 (date-fns/format @time-now "ss")]
     [:div (date-fns/format @time-now "aaa")]
     ]
                    
    (finally (js/clearInterval timer-fn))))

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
  ;  [:h2 "Clock Simple"]
  ;  [clock-simple]
   [:h2 "Clock Styled"]
   [clock-styled]
  ;  (date-fns/format (.getTime (js/Date.)) "MM/dd/yyyy")
  ;  [:br]
  ;  (date-fns/format (.now js/Date) "MM/dd/yyyy")
   ])

(defn clock-page-container []
  [:div
   [:h1 "clock Container"]
   [clock-panel]]
  )
