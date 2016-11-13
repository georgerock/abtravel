(ns abtravel.core
    (:require
   		[om.core :as om :include-macros true]
   		[om.dom :as dom :include-macros true]
        [goog.events :as events])
   	(:require-macros [cljs.core.async.macros :refer [go go-loop]])
   	(:import [goog.net XhrIo]
                goog.net.EventType
                [goog.events EventType]
                [goog.structs Map]
                [goog.Uri QueryData]))

(enable-console-print!)

(println "This text is printed from src/abtravel/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:buses []
                          :stations []
                          :lines []
                          :found-locations []}))

(defrecord Bus [id number lat long rev])
(defrecord Station [id coords tags])

(extend-type js/NodeList
	ISeqable
	(-seq [array] (array-seq array 0)))

(extend-type js/FileList
	ISeqable
	(-seq [array] (array-seq array 0)))

(extend-type js/HTMLCollection
	ISeqable
    (-seq [array] (array-seq array 0)))

(defn json-xhr [{:keys [method url data on-complete]}]
	(let [xhr (XhrIo.)
		  content-type (if (= method "GET")
   						"application/json"
   						"application/json")]
		(events/listen xhr goog.net.EventType.COMPLETE
			(fn [e]
				(on-complete (js->clj (if (= method "GET")
										  (.getResponseJson xhr)
										  (.getResponse xhr))))))
		(. xhr
            (send url method (when data (-> (clj->js data) (Map.) (QueryData.createFromMap) (.toString)))
		 	    #js {"Content-Type" content-type}))))

(defn parse-buses [res data]
    (let [arr (get res "rows")
         values (map #(get % "value") arr)
         keywordize (fn [b]
                        (let [id (get b "id")
                              number (get b "number")
                              lat (get b "lat")
                              long (get b "long")
                              rev (get b "rev")]
                              (Bus. id number lat long rev)))
         parsed-buses (map #(keywordize %) values)]
        (om/transact! data :buses (fn [_] parsed-buses))))

(defn xhr-request [method url data on-complete]
(json-xhr
	{:method method
	 :url url
	 :data data
	 :on-complete on-complete}))

(defn update-bus [bus]
    (let [id (:id bus)
          lat "1251251"
          long "1251612"
          number "SETESEE"
          rev (:rev bus)
          data {:number number :long long :lat lat :_rev rev}] (.log js/console (:id bus))
    (xhr-request "PUT" (str "http://172.17.0.2:5984/buses/" (:id bus) "/") (.stringify js/JSON (clj->js data))
        (fn [res] (.log js/console (clj->js res))))))

(defn get-something! [e data]
    (.stopPropagation e)
    (xhr-request "GET" "http://172.17.0.2:5984/buses/_design/buses/_view/buses" nil
        (fn [res]
            (parse-buses res data))))

(defn map-component [data owner]
    (reify
        om/IRender
        (render [_]
            (dom/p nil "vlad is here"))))

(defn search-component [data owner]
    (reify
        om/IRender
        (render [_]
            (dom/div #js {:className "row"}
                (dom/div #js {:className "col-sm-12"}
                    (dom/div #js {:className "jumbotron"}
                        (dom/div #js {:className "container-fluid"}
                            (dom/h4 nil "Statia - Casa Armatei")
                            (dom/p nil "AB 08 STS - 5 min")
                            (dom/p nil "Tu - 3 min")
                            (dom/div #js {:className "row"}
                                (dom/div #js {:className "col-xs-6"}
                                    (dom/button #js {:className "btn btn-primary btn-danger"
                                                     :onClick #(get-something! % data)} "Cumpara Bilet"))
                                (dom/div #js {:className "col-xs-6"} (.log js/console (clj->js (:buses data)))
                                    (dom/button #js {:className "btn btn-primary btn-success"
                                                     :onClick #(update-bus (first (:buses data)))} "Du-te in statie"))))))))))

(defn app-view [data owner]
    (reify
        om/IRender
        (render [_]
            (dom/div #js {:className "panel panel-default"}
                (dom/div #js {:className "panel-heading"}
                    (dom/h3 #js {:className "panel-title"} "ABTravel"))
                (dom/div #js {:className "panel-body"}
                    (dom/p nil "Where do you want to go?")
                    (dom/div #js {:className "row"}
                        (dom/div #js {:className "col-xs-12"}
                            (dom/div #js {:className "input-group"}
                                (dom/span #js {:className "input-group-btn"}
                                    (dom/button #js {:className "btn btn-default"}
                                        (dom/i #js {:className "fa fa-search"})))
                                (dom/input #js {:className "form-control"
                                                :type "text"
                                                :placeholder "Cauta..."}))))
                    (om/build search-component data)
                    (om/build map-component data))))))

(defn application [data owner]
	(reify
		om/IRender
		(render [_]
            (dom/div #js {:className "container-fluid"}
                (dom/div #js {:className "row"}
                    (dom/div #js {:className "col-xs-12"}
                    (om/build app-view data)))))))

(om/root application app-state
    {:target (. js/document (getElementById "app"))})

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
