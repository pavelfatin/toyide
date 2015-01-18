; Minimalistic web server.
; Select Run / Interpret (Shift + F10),
; then open http://localhost/ in a browser.

(def base-directory "./doc")

(def port 80)

(defn uri-from [line]
  (let [tokens (split #(= % \space) line)]
    (nth tokens 1)))

(defn alias-of [path]
  (if (directory? path)
      (let [delegate (concat path "index.html")]
        (if (exists? delegate) delegate path))
      path))

(defn extension-from [uri]
  (last (split #(= % \.) uri)))

(def extension-to-type
  (list "html" "text/html"
        "css" "text/css"
        "js" "application/javascript"
        "jpg" "image/jpeg"
        "png" "image/png"
        "gif" "image/gif"))

(defn content-type-for [extension]
  (get extension-to-type extension "text/html"))

(defn read-file [path]
  (let [file (open path),
        data (read file)]
    (close file)
    data))

(defn enumerate [path]
  (let [entries (dir path),
        is-directory #(directory? (concat path "/" %)),
        [directories files] (separate is-directory entries)]
    (concat (map #(concat % "/") directories) files)))

(defn read-index [path]
  (concat "<html><head><title>Index of " path "</title></head>"
          "<h1>Index of " path "</h1><hr><pre>"
          (mapcat #(concat "<a href='./" % "'>" % "</a><br>")
                  (enumerate path))
          "</pre><hr></body></html>"))

(defn load [path reader]
  (if (exists? path)
      (list "200 OK"
            (reader path))
      (list "404 Not Found"
            (concat "<html><h1>Not found: " path  "</h1></html>"))))

(defn format-response-header [code type length]
  (concat "HTTP/1.0 " code "\n"
          "Cache-Control: max-age=3600\n"
          "Content-Type: " type "\n"
          "Content-Length: " (format length) "\n\n"))

(defn log-request [socket uri type]
  (print (concat "Request from: " (format socket)
                 ", URI: " uri " (" type ") ")))

(defn log-content [length]
  (print (concat (format length) " bytes ")))

(defn log-response [code]
  (println (concat "- " code)))

(defn read-lines [socket]
  (let [line (read socket \newline)]
    (if (drop 2 line)
        (cons line (read-lines socket)))))

(defn handle-connection [socket]
  (if-let [lines (read-lines socket)]
    (let [uri (uri-from (first lines)),
          path (alias-of (concat base-directory uri)),
          type (content-type-for (extension-from path))]
      (log-request socket uri type)
      (let [reader (if (directory? path) read-index read-file),
            [code content] (load path reader),
            length (count content)]
        (log-content length)
        (write socket (format-response-header code type length))
        (flush socket)
        (write socket content)
        (log-response code))))
  (close socket))

(println "Web server initialized, waiting for connections...")
(println "Please open http://localhost/ in a browser.")

(listen port handle-connection)
