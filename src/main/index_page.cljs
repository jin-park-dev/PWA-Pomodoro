(ns index-page
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as rf]
   [util.dev :refer [dev-panel]]
   [stylefy.core :as stylefy :refer [use-style]]
   [component.style :refer [fn-animate-css]]
   [state.index]))

(defn index-panel []
  (let [css-h1-intro (if @(rf/subscribe [:index-initial?])
                       (reagent/atom "animate__flipInX")
                       (reagent/atom nil)  ; ;or empty string better? ""
                       )]
    (fn []
      [:div.flex-center.h-full
       [:h1.cursor-pointer.select-none.animate__animated {:class @css-h1-intro
                                                          :on-click (fn [] (fn-animate-css css-h1-intro "animate__flipInX" nil 1200))}
        "Welcome to PWA-Pomo"]
       [:div.text-center
        [:p "Simple no nonsense Pomodoro Timer"]
        [:p "Powered by Progressive Web App"]]
       [:button.btn.btn-nav.mt-5 "Start"]
       (when @(rf/subscribe [:dev?]) [dev-panel [css-h1-intro]])])))

(defn index-page-container []
  (reagent/with-let []
    [:main [index-panel]]
    (finally (rf/dispatch [:user-initial-visit false]))))


;; [:footer "The footer"]
