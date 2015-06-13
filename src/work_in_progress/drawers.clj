(ns work-in-progress.drawers
  (:require [quil.core :as q]))

(defn draw-phase-zero [state]
  (doseq [shape (:items state)]
    (q/fill (:fill-color shape))
    (q/stroke (:stroke-color shape))
    (q/stroke-weight (:border shape))
    (case (:type shape)
      :circle (q/ellipse (:x shape) (:y shape) (:radius shape) (:radius shape))
      :rect (q/rect (:x shape) (:y shape) (:width shape) (:height shape))
      )
    )
  )

(defn draw-phase-one [state]
  (q/text-size 50)
  (q/fill 0)
  (q/stroke 0)
  (q/text (:text state) 50 50))

(defn draw-phase-two [state]
  (q/shader (:shader state))
  (.set q/shader "iGlobalTime" (float (/ (q/millis) 1000)))
  (q/rect 0 0 (q/width) (q/height))
  )


