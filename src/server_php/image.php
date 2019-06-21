<?php
	$host='localhost';
	$uname='root';
	$pwd='1234';
	$db="pro";

$con = mysqli_connect($host,$uname,$pwd) or die("connection failed");
	mysqli_select_db($con,$db) or die("db selection failed");
	
	$UserID=filter_input(INPUT_POST,'UserID');
        $Create_time=filter_input(INPUT_POST,'Create_time');
        $x_coordinate=filter_input(INPUT_POST,'x_coordinate');
        $y_coordinate=filter_input(INPUT_POST,'y_coordinate');
        $hashtag=filter_input(INPUT_POST,'hashtag');
        $starpoint=filter_input(INPUT_POST,'starpoint');
        $text=filter_input(INPUT_POST,'text');
        
	$imagedevice=filter_input(INPUT_POST,'image');
	$data=base64_decode($imagedevice);
	$escaped_values=mysqli_real_escape_string($con,$data);
        
	$r=mysqli_query($con,"insert into picture (user_id,IMAGE,create_time,x_coordinate,y_coordinate,hashtag,starpoint,text) value('$UserID','$escaped_values','$Create_time','$x_coordinate','$y_coordinate','$hashtag','$starpoint','$text')");

	print(json_encode($r));
	mysqli_close($con);
?>