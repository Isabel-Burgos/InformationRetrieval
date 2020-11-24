/*     */ package org.apache.nutch.parsefilter.naivebayes;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.io.Reader;
/*     */ import java.lang.invoke.MethodHandles;
/*     */ import java.util.ArrayList;
/*     */ import org.apache.hadoop.conf.Configuration;
/*     */ import org.apache.hadoop.fs.FileSystem;
/*     */ import org.apache.hadoop.fs.Path;
/*     */ import org.apache.hadoop.util.StringUtils;
/*     */ import org.apache.nutch.parse.HTMLMetaTags;
/*     */ import org.apache.nutch.parse.HtmlParseFilter;
/*     */ import org.apache.nutch.parse.Outlink;
/*     */ import org.apache.nutch.parse.Parse;
/*     */ import org.apache.nutch.parse.ParseResult;
/*     */ import org.apache.nutch.protocol.Content;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ import org.w3c.dom.DocumentFragment;
/*     */ 
/*     */ public class NaiveBayesParseFilter implements HtmlParseFilter {
/*  51 */   private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
/*     */   
/*     */   public static final String TRAINFILE_MODELFILTER = "parsefilter.naivebayes.trainfile";
/*     */   
/*     */   public static final String DICTFILE_MODELFILTER = "parsefilter.naivebayes.wordlist";
/*     */   
/*     */   private Configuration conf;
/*     */   
/*     */   private String inputFilePath;
/*     */   
/*     */   private String dictionaryFile;
/*     */   
/*  59 */   private ArrayList<String> wordlist = new ArrayList<>();
/*     */   
/*     */   public boolean filterParse(String text) {
/*     */     try {
/*  64 */       return classify(text);
/*  65 */     } catch (IOException e) {
/*  66 */       LOG.error("Error occured while classifying:: " + text + " ::" + 
/*  67 */           StringUtils.stringifyException(e));
/*  70 */       return false;
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean filterUrl(String url) {
/*  75 */     return containsWord(url, this.wordlist);
/*     */   }
/*     */   
/*     */   public boolean classify(String text) throws IOException {
/*  82 */     if (Classify.classify(text).equals("1"))
/*  83 */       return true; 
/*  84 */     return false;
/*     */   }
/*     */   
/*     */   public void train() throws Exception {
/*  89 */     if (!FileSystem.get(this.conf).exists(new Path("naivebayes-model"))) {
/*  90 */       LOG.info("Training the Naive Bayes Model");
/*  91 */       Train.start(this.inputFilePath);
/*     */     } else {
/*  93 */       LOG.info("Model file already exists. Skipping training.");
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean containsWord(String url, ArrayList<String> wordlist) {
/*  98 */     for (String word : wordlist) {
/*  99 */       if (url.contains(word))
/* 100 */         return true; 
/*     */     } 
/* 104 */     return false;
/*     */   }
/*     */   
/*     */   public void setConf(Configuration conf) {
/* 108 */     this.conf = conf;
/* 109 */     this.inputFilePath = conf.get("parsefilter.naivebayes.trainfile");
/* 110 */     this.dictionaryFile = conf.get("parsefilter.naivebayes.wordlist");
/* 111 */     if (this.inputFilePath == null || this.inputFilePath.trim().length() == 0 || this.dictionaryFile == null || this.dictionaryFile
/* 112 */       .trim().length() == 0) {
/* 113 */       String message = "ParseFilter: NaiveBayes: trainfile or wordlist not set in the parsefilte.naivebayes.trainfile or parsefilte.naivebayes.wordlist";
/* 114 */       if (LOG.isErrorEnabled())
/* 115 */         LOG.error(message); 
/* 117 */       throw new IllegalArgumentException(message);
/*     */     } 
/*     */     try {
/* 120 */       if (FileSystem.get(conf).exists(new Path(this.inputFilePath)) || 
/* 121 */         FileSystem.get(conf).exists(new Path(this.dictionaryFile))) {
/* 122 */         String message = "ParseFilter: NaiveBayes: " + this.inputFilePath + " or " + this.dictionaryFile + " not found!";
/* 124 */         if (LOG.isErrorEnabled())
/* 125 */           LOG.error(message); 
/* 127 */         throw new IllegalArgumentException(message);
/*     */       } 
/* 130 */       BufferedReader br = null;
/* 133 */       Reader reader = conf.getConfResourceAsReader(this.dictionaryFile);
/* 134 */       br = new BufferedReader(reader);
/*     */       String CurrentLine;
/* 135 */       while ((CurrentLine = br.readLine()) != null)
/* 136 */         this.wordlist.add(CurrentLine); 
/* 139 */     } catch (IOException e) {
/* 140 */       LOG.error(StringUtils.stringifyException(e));
/*     */     } 
/*     */     try {
/* 143 */       train();
/* 144 */     } catch (Exception e) {
/* 146 */       LOG.error("Error occured while training:: " + 
/* 147 */           StringUtils.stringifyException(e));
/*     */     } 
/*     */   }
/*     */   
/*     */   public Configuration getConf() {
/* 154 */     return this.conf;
/*     */   }
/*     */   
/*     */   public ParseResult filter(Content content, ParseResult parseResult, HTMLMetaTags metaTags, DocumentFragment doc) {
/* 161 */     Parse parse = parseResult.get(content.getUrl());
/* 163 */     String url = content.getBaseUrl();
/* 164 */     ArrayList<Outlink> tempOutlinks = new ArrayList<>();
/* 165 */     String text = parse.getText();
/* 167 */     if (!filterParse(text)) {
/* 170 */       LOG.info("ParseFilter: NaiveBayes: Page found irrelevant:: " + url);
/* 171 */       LOG.info("Checking outlinks");
/* 173 */       Outlink[] out = null;
/*     */       int i;
/* 174 */       for (i = 0; i < (parse.getData().getOutlinks()).length; i++) {
/* 175 */         LOG.info("ParseFilter: NaiveBayes: Outlink to check:: " + parse
/* 176 */             .getData().getOutlinks()[i].getToUrl());
/* 177 */         if (filterUrl(parse.getData().getOutlinks()[i].getToUrl())) {
/* 178 */           tempOutlinks.add(parse.getData().getOutlinks()[i]);
/* 179 */           LOG.info("ParseFilter: NaiveBayes: found relevant");
/*     */         } else {
/* 182 */           LOG.info("ParseFilter: NaiveBayes: found irrelevant");
/*     */         } 
/*     */       } 
/* 185 */       out = new Outlink[tempOutlinks.size()];
/* 186 */       for (i = 0; i < tempOutlinks.size(); i++)
/* 187 */         out[i] = tempOutlinks.get(i); 
/* 189 */       parse.getData().setOutlinks(out);
/*     */     } else {
/* 192 */       LOG.info("ParseFilter: NaiveBayes: Page found relevant:: " + url);
/*     */     } 
/* 195 */     return parseResult;
/*     */   }
/*     */ }


/* Location:              C:\cygwin64\home\apache-nutch-1.17\plugins\parsefilter-naivebayes\parsefilter-naivebayes.jar!\org\apache\nutch\parsefilter\naivebayes\NaiveBayesParseFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */