(ns work-in-progress.drawers
  (:require [quil.core :as q]
            [work-in-progress.audio :as a]))

(defn draw-phase-zero [state]
  (if (= (mod (q/frame-count) 10) 0) (a/play-back "resources/Clap-2.wav") "")
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
  (q/stroke-weight 0)
  (.set (:shader state) "iGlobalTime" (float (/ (q/millis) 1000)))
  (q/shader (:shader state))
  (q/ellipse (:x state) (:y state) (:width state) (:height state)))

(defn draw-box [pos rot shader]
  (q/push-matrix)
  (apply q/translate pos)
  (q/rotate-x (* 0.0005 (q/millis)))
  (q/rotate-y rot)
  (.set shader "iGlobalTime" (float (/ (q/millis) 1000)))
  (q/shader shader)
  (q/box 100)
  (q/pop-matrix)
  )

(defn draw-phase-three [state]
  (.set (:shader state) "iGlobalTime" (float (/ (q/millis) 1000)))
  (q/shader (:shader state))
  (q/rect 0 0 (q/width) (q/height))
  (draw-box (:box-center state) (:box-rotation state) (:box-shader state)))

(defn draw-branch [branch wind gravity]
  (q/push-matrix)
  (q/rotate-z (+ (* (:dir branch) gravity) wind))
  (q/stroke-weight (:width branch))
  (q/line 0 0 0 (* 1.0 (:length branch)))
  (q/translate 0 (* 1.0 (:length branch)))
  (doseq [child (:chields branch)]
    (if child
      (draw-branch child wind gravity)))
  (q/pop-matrix))

(defn draw-phase-four [state]
  (.set (:shader state) "iGlobalTime" (float (/ (q/millis) 1000)))
  (q/shader (:shader state))
  (q/rect 0 0 (q/width) (q/height))
  (q/reset-shader)
  (q/push-matrix)
  (q/fill 0)
  (q/stroke 0)
  (q/translate (/ (q/width) 2) 0 0)
  (draw-branch (:root state) (:wind state) (:gravity state))
  (q/pop-matrix))
