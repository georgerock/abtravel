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

(defonce app-state (atom {:buses "asdf"
                          :stations []
                          :lines []}))

(defrecord Bus [id number coords])
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
   						"application/x-www-form-urlencoded-data; charset=UTF8")]
		(events/listen xhr goog.net.EventType.COMPLETE
			(fn [e]
				(on-complete (js->clj (if (= method "GET")
										  (.getResponseJson xhr)
										  (.getResponse xhr))))))
		(.send url method (when data (-> (clj->js data) (Map.) (QueryData.createFromMap) (.toString)))
		 	#js {"Content-Type" content-type})))

(defn xhr-request [method url data on-complete token]
(json-xhr
	{:method method
	 :url url
	 :data data
	 :on-complete on-complete
	 :token token}))

(defn app-view [data owner]
	(reify
		om/IRender
		(render [_]
            (dom/button #js {:className "btn btn-primary btn-danger"
                             :onClick #(.alert js/window "ha")} (str (:buses data))))))

(om/root app-view app-state
    {:target (. js/document (getElementById "app"))})

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
