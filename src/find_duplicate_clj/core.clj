(ns find-duplicate-clj.core
  (:require [clojure.java.io :as jio]
            [clojure.string :as str])
  (:import (java.io File)
           (org.apache.commons.codec.digest DigestUtils)))

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))


;String collect = Files.walk(Paths.get(root))
;.filter(Files::isRegularFile) // 去掉文件夹
;.filter(p -> p.toFile().length() > 0) // 去掉空文件
;.collect(Collectors.groupingBy(p -> p.toFile().length())) // 先按长度初步分组
;.values() // 只保留分组后的值，不关心长度
;.stream()
;.filter(l -> (l.size() >= 2)) // 只保留分组中元素超过2的，即可能有重复
;.flatMap(DuplicateFinder::groupByMd5) // 根据MD5二次筛选
;.filter(l -> (l.size() >= 2)) // 只保留分组中元素超过2的，即确定有重复
;.map(l -> String.join("\n", l.stream().map(Path::toString).toArray(String[]::new)))
;.collect(Collectors.joining("\n-----------------\n")); // 格式化输出
;
;Files.write(Paths.get("dup.txt"), collect.getBytes(StandardCharsets.UTF_8));

(defn- md5 [file]
  (DigestUtils/md5Hex (jio/input-stream file))
  )

(defn find-duplicate [root]
  (->> root
       jio/file
       file-seq
       (filter #(.isFile ^File %))
       (filter #(pos? (.length %)))
       (group-by #(.length %))
       vals
       (filter #(>= (count %) 2))
       (mapcat #(group-by md5 %))
       vals
       (filter #(>= (count %) 2))
       (map #(str/join "\n" %))
       (str/join "\n--------\n")
       (spit "dup.txt")
       )
  )