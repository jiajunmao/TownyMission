#!/bin/sh

mvn -s .gitlab/ci_settings.xml -T 16 clean install
