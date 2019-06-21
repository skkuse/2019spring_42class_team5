<?php
    $con=mysqli_connect("localhost","root","1234","pro");
 
    mysqli_set_charset($con,"utf8");
            
    if(mysqli_connect_error($con)){
        echo "Failed to connect MySQL: " . mysqli_connect_error();
    }
	
	$UserID=filter_input(INPUT_POST,'UserID');
        $Create_time=filter_input(INPUT_POST,'Create_time');
        
	$result=mysqli_query($con,"select * from picture where user_id='$UserID'");
        $arr=array();    
        $cnt=0;
        while($row=mysqli_fetch_array($result)){
            $count=$cnt;
            $arr[$count]['user_id']=$row[0];      
            $arr[$count]['image']=base64_encode($row[1]);     
            $arr[$count]['create_time']=$row[2]; 
            $arr[$count]['x_coordinate']=$row[3];
            $arr[$count]['y_coordinate']=$row[4];
            $arr[$count]['hashtag']=$row[5];
            $arr[$count]['starpoint']=$row[6];
            $arr[$count]['text']=$row[7];
            $cnt++;
        }
        print(json_encode($arr));

	mysqli_close($con);
?>