/*     */ package org.apache.nutch.parsefilter.naivebayes;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.Writer;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import org.apache.hadoop.conf.Configuration;
/*     */ import org.apache.hadoop.fs.FileSystem;
/*     */ import org.apache.hadoop.fs.Path;
/*     */ 
/*     */ public class Train {
/*     */   public static String replacefirstoccuranceof(String tomatch, String line) {
/*  35 */     int index = line.indexOf(tomatch);
/*  36 */     if (index == -1)
/*  37 */       return line; 
/*  39 */     return line.substring(0, index) + line
/*  40 */       .substring(index + tomatch.length());
/*     */   }
/*     */   
/*     */   public static void updateHashMap(HashMap<String, Integer> dict, String key) {
/*  46 */     if (!key.equals(""))
/*  47 */       if (dict.containsKey(key)) {
/*  48 */         dict.put(key, Integer.valueOf(((Integer)dict.get(key)).intValue() + 1));
/*     */       } else {
/*  50 */         dict.put(key, Integer.valueOf(1));
/*     */       }  
/*     */   }
/*     */   
/*     */   public static String flattenHashMap(HashMap<String, Integer> dict) {
/*  55 */     String result = "";
/*  57 */     for (String key : dict.keySet())
/*  59 */       result = result + key + ":" + dict.get(key) + ","; 
/*  63 */     result = result.substring(0, result.length() - 1);
/*  65 */     return result;
/*     */   }
/*     */   
/*     */   public static void start(String filepath) throws IOException {
/*  75 */     int numof_ir = 0;
/*  76 */     int numof_r = 0;
/*  77 */     int numwords_ir = 0;
/*  78 */     int numwords_r = 0;
/*  79 */     HashSet<String> uniquewords = new HashSet<>();
/*  80 */     HashMap<String, Integer> wordfreq_ir = new HashMap<>();
/*  81 */     HashMap<String, Integer> wordfreq_r = new HashMap<>();
/*  83 */     String line = "";
/*  84 */     String target = "";
/*  85 */     String[] linearray = null;
/*  88 */     Configuration configuration = new Configuration();
/*  89 */     FileSystem fs = FileSystem.get(configuration);
/*  92 */     BufferedReader bufferedReader = new BufferedReader(configuration.getConfResourceAsReader(filepath));
/*  94 */     while ((line = bufferedReader.readLine()) != null) {
/*  96 */       target = line.split("\t")[0]; //get the number of the line
/*  98 */       line = replacefirstoccuranceof(target + "\t", line); //get rid of the number and tab
/* 100 */       linearray = line.replaceAll("[^a-zA-Z ]", "").toLowerCase().split(" "); //get rid of non-alphabetical characters and split words
/* 103 */       if (target.equals("0")) {
/* 105 */         numof_ir++;
/* 106 */         numwords_ir += linearray.length;
/* 107 */         for (int j = 0; j < linearray.length; j++) {
/* 108 */           uniquewords.add(linearray[j]);
/* 109 */           updateHashMap(wordfreq_ir, linearray[j]);
/*     */         } 
/*     */         continue; // breaks one iteration of the loop
/*     */       } 
/* 113 */       numof_r++;
/* 114 */       numwords_r += linearray.length;
/* 115 */       for (int i = 0; i < linearray.length; i++) {
/* 116 */         uniquewords.add(linearray[i]);
/* 117 */         updateHashMap(wordfreq_r, linearray[i]);
/*     */       } 
/*     */     } 
							// This writes in a file the preprocessed data
/* 126 */     Path path = new Path("naivebayes-model");
/* 128 */     Writer writer = new BufferedWriter(new OutputStreamWriter((OutputStream)fs.create(path, true)));
/* 131 */     writer.write(String.valueOf(uniquewords.size()) + "\n");
/* 132 */     writer.write("0\n");
/* 133 */     writer.write(String.valueOf(numof_ir) + "\n");
/* 134 */     writer.write(String.valueOf(numwords_ir) + "\n");
/* 135 */     writer.write(flattenHashMap(wordfreq_ir) + "\n");
/* 136 */     writer.write("1\n");
/* 137 */     writer.write(String.valueOf(numof_r) + "\n");
/* 138 */     writer.write(String.valueOf(numwords_r) + "\n");
/* 139 */     writer.write(flattenHashMap(wordfreq_r) + "\n");
/* 141 */     writer.close();
/* 143 */     bufferedReader.close();
/*     */   }
/*     */ }


/* Location:              C:\cygwin64\home\apache-nutch-1.17\plugins\parsefilter-naivebayes\parsefilter-naivebayes.jar!\org\apache\nutch\parsefilter\naivebayes\Train.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */