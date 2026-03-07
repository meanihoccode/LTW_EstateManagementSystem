@echo off
cd /d D:\LTW_QuanLyBDS\LTW_QuanLyBDS
timeout /t 5 /nobreak
curl -X GET http://localhost:8080/api/owners/4

