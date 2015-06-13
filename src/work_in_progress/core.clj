(ns work-in-progress.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [work-in-progress.dynamic :as dynamic])
;  (:gen-class)
)

(defn -main []
  (q/sketch
    :title "Work in Progress"
    :setup dynamic/setup
    :draw dynamic/draw
    :update dynamic/update
    :size [640 480]
    :renderer :p2d
    :middleware [m/fun-mode]))

