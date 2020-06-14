(ns component.vis
  (:require
   #_[reagent.core :as reagent]))

(defn icon-array
  "Creates Icon array.
   Leverages grid with tailwindcss.
   To customize colour, use text-color in above component.
   Currently customize by changing font style. (Upgrade to svg/icon if needed in future)
   "
  [{:keys [num row gap class]
    :or   {num 1 ; default to show it's active
           row 10 ; number of items in a row. 1-12 default settings in tailwind.
           gap 4 ; gap in grid
           class nil
           }}]
  (let [grid-cols (str "grid-cols-" row)
        _gap (str "gap-" gap)
        _class (str grid-cols " " _gap " " class)
        ]
    
    [:div.grid {:class _class}
     (doall
      (for [i (range num)]
        ^{:key i} [:div "o"]))]))