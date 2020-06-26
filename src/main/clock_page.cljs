(ns clock-page
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as rf]
   [stylefy.core :as stylefy :refer [use-style]]
   [date-fns :as date-fns]
   [component.clock :as clock]
   [component.style :refer [clock-digital-styled-vue--container-style]]))


(defn clock-digital-clean--container [style]
  (reagent/with-let [time-now (reagent/atom (.now js/Date))
                     timer-fn (js/setInterval #(reset! time-now (.now js/Date)) 10)
                     state (reagent/atom {:date-visible? false
                                          :ms-visible? false
                                          :dev? @(rf/subscribe [:dev?])})]
    [:div#clock-styled.btn-like {:on-click (fn []
                                             (swap! state update-in [:date-visible?] not)
                                             (swap! state update-in [:ms-visible?] not))}
     [:div.my-5 [clock/digital-clean {:time-now @time-now
                                      :date-visible? (get-in @state [:date-visible?])
                                      :ms-placement "bottom"
                                      :ms-visible? (get-in @state [:ms-visible?])
                                      :class @(rf/subscribe [:theme-time-style])}]]]
    (finally (js/clearInterval timer-fn))))


(defn clock-digital-styled-vue--container [style]
  (reagent/with-let [time-now (reagent/atom (.now js/Date))
                     timer-fn (js/setInterval #(reset! time-now (.now js/Date)) 10)
                     state (reagent/atom {:date-visible? false
                                          :ms-visible? false
                                          :dev? @(rf/subscribe [:dev?])})]
    [:div#clock-styled-vue.w-full.btn-like.rounded (use-style clock-digital-styled-vue--container-style
                                                      {:on-click (fn []
                                                                   (swap! state update-in [:date-visible?] not)
                                                                   (swap! state update-in [:ms-visible?] not))})
     [clock/digital-vue {:time-now @time-now
                         :date-visible? (get-in @state [:date-visible?])
                         :ms-placement "bottom"
                         :ms-visible? (get-in @state [:ms-visible?])
                         :class nil}]]

    (finally (js/clearInterval timer-fn))))


(defn clock-panel-nav []
  (let [nav-styled? (reagent/atom false)]
    (fn []
      [:div.flex.flex-col.items-center.h-full
       [:div.mb-5 (if @nav-styled? [:button.btn.btn-nav {:on-click #(swap! nav-styled? not)} "Styled"] [:button.btn.btn-nav {:on-click #(swap! nav-styled? not)} "Clean"])]
       [:div.flex-center.h-full.w-full (if @nav-styled? [clock-digital-styled-vue--container] [clock-digital-clean--container])]])))

(defn clock-page-container []
  [:main [clock-panel-nav]])



(comment
  ; usage of js date-fns package
  (date-fns/format (.getTime (js/Date.)) "MM/dd/yyyy")
  (date-fns/format (.now js/Date) "MM/dd/yyyy")
  (date-fns/format (.now js/Date) "h:mm:ss aaa"))