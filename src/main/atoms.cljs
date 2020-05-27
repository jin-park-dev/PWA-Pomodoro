(ns atoms
  (:require [reagent.core :as reagent]
            [clojure.string :as str]
            [re-frame.core :as rf]
            [day8.re-frame.http-fx]
            [ajax.core :as ajax]))

(defonce my-state (reagent/atom {}))
(defonce my-other (reagent/atom {}))

(defn sub-component [a]
  (fn [a]
    (js/console.log "2a. SUB-COMPONENT")
    [:div.my-5.pr-5
     [:h2.text-lg "2a. SUB-COMPONENT"]
     [:div a]
     [:div @my-other]]))

(defn sub-component-local-state [a]
  (let [local-s (reagent/atom 0)]
    (js/setInterval #(swap! local-s inc) 1000)
    (fn [a]
      (js/console.log "2b. SUB-COMPONENT-LOCAL-STATE")
      [:div.my-5.pr-5
       [:h2.text-lg "2b. SUB-COMPONENT-LOCAL-STATE"]
       [:div "a: " a]
       [:div "local-s: " @local-s]
       [:div "@my-other: " @my-other]])))

(defn atoms-panel []
  (js/console.log "2. ATOMS-PANEL")
  [:div.my-5
   [:h2.text-lg "2. ATOMS-PANEL"]
   [:h1 "Atoms"]
;    [sub-component (:age @my-state)]
;    [sub-component (:first-name @my-state)]
     [sub-component-local-state (:first-name @my-state)]
;    [sub-component (:name @my-other)]
;    [sub-component 2]
   ])

(defn atoms-container []
  (js/console.log "1. ATOMS-CONTAINER")
  [:<>
   [:h2.text-lg.my-5 "1. ATOMS-CONTAINER"]
   [atoms-panel]
   #_[:div (pr-str (deref my-state))] ; (pr-str @my-state) is shortcut to deref.
   [:hr.my-6]
   [:div.bg-white.rounded-lg.border-gray-300.border.p-3.shadow-lg
    [:div.flex.flex-col
     [:h1.text-3xl "Dev Log"]
     [:div.flex.flex-row.mt-3
      [:h2.text-lg {:class "w-2/12"} "@my-state: "]
      [:div {:class "w-10/12"} (pr-str @my-state)]]
     [:div.flex.flex-row.mt-3
      [:h2.text-lg {:class "w-2/12"} "@my-other: "]
      [:div {:class "w-10/12"} (pr-str @my-other)]]]] 
   ])

(comment
  
  ;;-- run these to see how atom works.
  
  (js/console.log "Hello")
  
  ;unconditionally sets it to new state. (Throws away old stuff)
  (reset! my-state {:name "Jin"})
  (reset! my-state {})
  
  
  (reset! my-state 0)
  (swap! my-state inc)
  
  
  (reset! my-state {})
  (swap! my-state assoc :first-name "Jin")
  (swap! my-state assoc :sur-name "Park")
  (swap! my-state assoc :age 15)
  (swap! my-state update :age inc)
  (update {} :pets conj :dog)
  (swap! my-state update :age inc)
  
  
  (reset! my-state {})
  (swap! my-state assoc-in [:people 1] {:name "Genie"
                                        :age 21})
  (swap! my-state update-in [:people 1 :age] inc)
  


  (reset! my-other "Hello!!! new")
  (swap! my-state assoc :first-name "Jin")
  (swap! my-state assoc :first-name "John")
  )