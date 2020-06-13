(ns app
  (:require
   [reagent.core :as r]
   [reagent.dom :as d]
   [re-frame.core :as rf]
   [state.events :as events]

   [index-page :refer (index-page-container)]
   [pomodoro-page :refer (pomodoro-page-container)]
   [clock-page :refer (clock-page-container)]
   [timer-page :refer (timer-page-container)]

   ["react-router-dom" :refer (Route NavLink BrowserRouter)]

   [stylefy.core :as stylefy])
  )


;; react-router wants react component classes
(def IndexPageContainer (r/reactify-component index-page-container))
(def PomodoroPageContainer (r/reactify-component pomodoro-page-container))
(def ClockPageContainer (r/reactify-component clock-page-container))
(def TimerPageContainer (r/reactify-component timer-page-container))

(defn root []
  [:> BrowserRouter
   [:div.grid-style {:class @(rf/subscribe [:theme/general-text 500])}
    [:nav
     [:ul.nav-container
      [:li.mr-2.sm:mr-6
       [:> NavLink {:to "/"
                    :active-class-name "nav-link--active"
                    :exact true
                    :class "nav-btn nav-link"} "Home"]]
      [:li.mr-2.sm:mr-6
       [:> NavLink {:to "/clock/"
                    :activeClassName "nav-link--active"
                    :class "nav-btn nav-link"} "Clock"]]
      [:li.mr-2.sm:mr-6
       [:> NavLink {:to "/timer/"
                    :activeClassName "nav-link--active"
                    :class "nav-btn nav-link"} "Timer"]]
      [:li
       [:> NavLink {:to "/pomodoro/"
                    :activeClassName "nav-link--active"
                    :class "nav-btn nav-link"} "Pomodoro"]]
      (when @(rf/subscribe [:dev-panel?])
        [:li.mr-6.cursor-pointer.text-sm
         [:a.nav-btn.nav-link.text-indigo-400
          {:on-click (fn [e]
                       (.preventDefault e)
                      ;;  (rf/dispatch [:dev/dev-panel-switch])
                       (rf/dispatch [:dev/dev-switch])
                       )}
          ;; TODO: Enable/disable menu which also enables dev in future. Right now panel doesn't do aynthing so leaving this as general dev mode on/off
          ;; "dev-menu:" (pr-str @(rf/subscribe [:dev-panel?]))
          "dev-menu:" (pr-str @(rf/subscribe [:dev?]))
          ]])]]

    [:> Route {:path "/" :exact true :component IndexPageContainer}]
    [:> Route {:path "/clock/" :component ClockPageContainer}]
    [:> Route {:path "/timer/" :component TimerPageContainer}]
    [:> Route {:path "/pomodoro/" :component PomodoroPageContainer}]]])


(defn ^:dev/after-load start []
  (d/render [root] (js/document.getElementById "root")))

; Entry point to our app for shadow-cljs system.
(defn init []
  
  ; Add other initalization here if needed such as ajax call for inital data
  (rf/dispatch-sync [::events/initialize-db])  ; Difference with dispatch is this is synchronous (unlike dispatch which is asynchronous). Guarantees this line is done before next.
  (rf/dispatch-sync [::events/initialize-db-user-settings]) ; Default user settings
  ; (rf/dispatch-sync [::events/initialize-db-dev]) ; Inital dev setting
  (start) ; Reagent (react)
  (stylefy/init))
