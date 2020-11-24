/*     */ package org.apache.nutch.parsefilter.naivebayes;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.util.HashMap;
/*     */ import org.apache.hadoop.conf.Configuration;
/*     */ import org.apache.hadoop.fs.FileSystem;
/*     */ import org.apache.hadoop.fs.Path;
/*     */ 
/*     */ public class Classify {
/*  29 */   private static int uniquewords_size = 0;
/*     */   
/*  31 */   private static int numof_ir = 0;
/*     */   
/*  32 */   private static int numwords_ir = 0;
/*     */   
/*  33 */   private static HashMap<String, Integer> wordfreq_ir = null;
/*     */   
/*  35 */   private static int numof_r = 0;
/*     */   
/*  36 */   private static int numwords_r = 0;
/*     */   
/*  37 */   private static HashMap<String, Integer> wordfreq_r = null;
/*     */   
/*     */   private static boolean ismodel = false;
/*     */   
/*     */   public static HashMap<String, Integer> unflattenToHashmap(String line) {
/*  41 */     HashMap<String, Integer> dict = new HashMap<>();
/*  43 */     String[] dictarray = line.split(",");
/*  45 */     for (String field : dictarray)
/*  47 */       dict.put(field.split(":")[0], Integer.valueOf(field.split(":")[1])); 
/*  50 */     return dict;
/*     */   }
/*     */   
/*     */   public static String classify(String line) throws IOException {
/*  56 */     double prob_ir = 0.0D;
/*  57 */     double prob_r = 0.0D;
/*  59 */     String result = "1";
/*  62 */     String[] linearray = line.replaceAll("[^a-zA-Z ]", "").toLowerCase().split(" ");
/*  66 */     if (!ismodel) {
/*  67 */       Configuration configuration = new Configuration();
/*  68 */       FileSystem fs = FileSystem.get(configuration);
/*  71 */       BufferedReader bufferedReader = new BufferedReader(new InputStreamReader((InputStream)fs.open(new Path("naivebayes-model"))));
/*  73 */       uniquewords_size = Integer.valueOf(bufferedReader.readLine()).intValue();
/*  74 */       bufferedReader.readLine();
/*  76 */       numof_ir = Integer.valueOf(bufferedReader.readLine()).intValue();
/*  77 */       numwords_ir = Integer.valueOf(bufferedReader.readLine()).intValue();
/*  78 */       wordfreq_ir = unflattenToHashmap(bufferedReader.readLine());
/*  79 */       bufferedReader.readLine();
/*  80 */       numof_r = Integer.valueOf(bufferedReader.readLine()).intValue();
/*  81 */       numwords_r = Integer.valueOf(bufferedReader.readLine()).intValue();
/*  82 */       wordfreq_r = unflattenToHashmap(bufferedReader.readLine());
/*  84 */       ismodel = true;
/*  86 */       bufferedReader.close();
/*     */     } 
/*  92 */     for (String word : linearray) {
/*  93 */       if (wordfreq_ir.containsKey(word)) {
/*  94 */         prob_ir += Math.log(((Integer)wordfreq_ir.get(word)).intValue()) + 1.0D - 
/*  95 */           Math.log((numwords_ir + uniquewords_size));
/*     */       } else {
/*  97 */         prob_ir += 1.0D - Math.log((numwords_ir + uniquewords_size));
/*     */       } 
/*  99 */       if (wordfreq_r.containsKey(word)) {
/* 100 */         prob_r += Math.log(((Integer)wordfreq_r.get(word)).intValue()) + 1.0D - 
/* 101 */           Math.log((numwords_r + uniquewords_size));
/*     */       } else {
/* 103 */         prob_r += 1.0D - Math.log((numwords_r + uniquewords_size));
/*     */       } 
/*     */     } 
/* 107 */     prob_ir += Math.log(numof_ir) - Math.log((numof_ir + numof_r));
/* 108 */     prob_r += Math.log(numof_r) - Math.log((numof_ir + numof_r));
/* 110 */     if (prob_ir > prob_r) {
/* 111 */       result = "0";
/*     */     } else {
/* 113 */       result = "1";
/*     */     } 
/* 115 */     return result;
/*     */   }
/*     */ }


/* Location:              C:\cygwin64\home\apache-nutch-1.17\plugins\parsefilter-naivebayes\parsefilter-naivebayes.jar!\org\apache\nutch\parsefilter\naivebayes\Classify.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */