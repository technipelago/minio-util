#!/bin/sh

./gradlew assemble

native-image --no-server --no-fallback --native-image-info --verbose -cp build/libs/minio-util-1.0.0.BUILD-SNAPSHOT-all.jar se.technipelago.minio.cmd.MainCommand mu
#native-image --no-server --no-fallback --native-image-info --verbose --report-unsupported-elements-at-runtime -H:+ReportExceptionStackTraces -cp build/libs/minio-util-1.0.0.BUILD-SNAPSHOT-all.jar se.technipelago.minio.cmd.MainCommand mu
