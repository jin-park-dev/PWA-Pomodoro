(ns app
  (:require
   [reagent.core :as r]
   [reagent.dom :as d]
   
   [index-page :refer (index-page-container)]
   [pomodoro-page :refer (pomodoro-page-container)]
   [clock-page :refer (clock-page-container)]
   [timer-page :refer (timer-page-container)]
   
   ["react-router-dom" :refer (Route NavLink BrowserRouter)]
   
   [stylefy.core :as stylefy]
   ))

;; react-router wants react component classes
(def IndexPageContainer (r/reactify-component index-page-container))
(def PomodoroPageContainer (r/reactify-component pomodoro-page-container))
(def ClockPageContainer (r/reactify-component clock-page-container))
(def TimerPageContainer (r/reactify-component timer-page-container))

(defn root []
  [:> BrowserRouter
   [:div.grid-style
    [:nav.border-b-2.border-solid.border-gray-500
     [:ul.flex.flex-row
      [:li.mr-6
       [:> NavLink {:to "/"
                    :active-class-name "nav-link--active"
                    :exact true
                    :class "nav nav-link"} "Home"]]
      [:li.mr-6
       [:> NavLink {:to "/clock/"
                    :activeClassName "nav-link--active"
                    :class "nav nav-link"} "Clock"]]
      [:li.mr-6
       [:> NavLink {:to "/timer/"
                    :activeClassName "nav-link--active"
                    :class "nav nav-link"} "Timer"]]
      [:li.mr-6
       [:> NavLink {:to "/pomodoro/"
                    :activeClassName "nav-link--active"
                    :class "nav nav-link"} "Pomodoro"]]
      ]]

    [:> Route {:path "/" :exact true :component IndexPageContainer}]
    [:> Route {:path "/clock/" :component ClockPageContainer}]
    [:> Route {:path "/timer/" :component TimerPageContainer}]
    [:> Route {:path "/pomodoro/" :component PomodoroPageContainer}]
    ]])

(defn ^:dev/after-load start []
  (d/render [root] (js/document.getElementById "root")))


; Entry point to our app for shadow-cljs system.
(defn init []
  ; Add other initalization here if needed such as ajax call for inital data
  (start) ; Reagent (react)
  (stylefy/init)
  )
