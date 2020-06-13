(ns index-page
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as rf]
   [util.dev :refer [dev-panel]]
   [component.style :refer [fn-animate-css]]
   [component.theme :as theme]
   [state.index]
   [state.db :refer [theme-colors]]
   ["react-router-dom" :refer (Link)]))


;; (def theme-actions (map (fn [color] (keyword color)) theme-colors))

(defn index-panel []
  (let [css-h1-intro (if @(rf/subscribe [:index-initial?])
                       (reagent/atom "animate__flipInX")
                       (reagent/atom nil)  ; ;or empty string better? ""
                       )]
    (fn []
      [:div.flex-center.h-full
       [:h1.text-center.cursor-pointer.select-none.animate__animated {:class @css-h1-intro
                                                          :on-click (fn [] (fn-animate-css css-h1-intro "animate__flipInX" nil 1200))}
        "Welcome to PWA-Pomo"]
       [:div.text-center
        [:p "Simple no nonsense Pomodoro Timer"]
        [:p "Powered by Progressive Web App"]]
       [:> Link {:to "pomodoro" :class "my-5"} [:button.btn.btn-nav "Start"]]
       [theme/picker theme-colors]
       (when @(rf/subscribe [:dev?]) [dev-panel [css-h1-intro]])])))

(defn index-page-container []
  (reagent/with-let []
    [:main [index-panel]]
    (finally (rf/dispatch [:user-initial-visit false]))))


;; [:footer "The footer"]
