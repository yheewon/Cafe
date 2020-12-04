<?php
  define('HOST','localhost');
  define('USER','heewon');
  define('PASS','gmldnjs');
  define('DB','cafedb');

  $con = mysqli_connect(HOST,USER,PASS,DB) or die('DB에 연결할수 없습니다.');
?>
