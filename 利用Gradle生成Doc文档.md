```
task javadoc(type: Javadoc) {
     source = android.sourceSets.main.java.srcDirs
 classpath += project.files(android.getBootClasspath().join(File.pathSeparator))
}

javadoc {
    options {
        encoding "UTF-8"
        charSet 'UTF-8'
        author true
        version true
        links "http://docs.oracle.com/javase/7/docs/api"
    }
}
```

完美，上面的也能解决文档乱码问题