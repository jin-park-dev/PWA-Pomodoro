(ns app
  (:require
   [reagent.core :as r]
   [reagent.dom :as d]
   
   [index-page :refer (index-page-container)]
   [pomodoro-page :refer (pomodoro-page-container)]
   [clock-page :refer (clock-page-container)]
   [timer-page :refer (timer-page-container)]

    ;; don't need the :refer or :rename
    ;; could just use :as router and then [:> router/Link ...]
    ;; just wanted to match the JS example
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
   [:div.p-10
    [:nav.mb-6.border-b-2.border-solid.border-gray-500
     [:ul.flex.mb-2
      [:li.mr-6
       [:> NavLink {:to "/"
                    :active-class-name "border-blue-500 bg-blue-500 text-white hover:text-black"
                    :exact true
                    :class "inline-block border border-white rounded hover:border-blue-200 hover:bg-blue-200 no-underline py-1 px-3"} "Home"]]
      [:li.mr-6
       [:> NavLink {:to "/clock/"
                    :activeClassName "border-blue-500 bg-blue-500 text-white hover:text-black"
                    :class "inline-block border border-white rounded hover:border-blue-200 hover:bg-blue-200 no-underline py-1 px-3"} "Clock"]]
      [:li.mr-6
       [:> NavLink {:to "/timer/"
                    :activeClassName "border-blue-500 bg-blue-500 text-white hover:text-black"
                    :class "inline-block border border-white rounded hover:border-blue-200 hover:bg-blue-200 no-underline py-1 px-3"} "Timer"]]
      [:li.mr-6
       [:> NavLink {:to "/pomodoro/"
                    :activeClassName "border-blue-500 bg-blue-500 text-white hover:text-black"
                    :class "inline-block border border-white rounded hover:border-blue-200 hover:bg-blue-200 no-underline py-1 px-3"} "Pomodoro"]]
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
  (stylefy/init)  ; 
  )
