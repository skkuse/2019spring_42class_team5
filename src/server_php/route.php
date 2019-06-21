<?php
    $con=mysqli_connect("localhost","root","1234","pro");
 
    mysqli_set_charset($con,"utf8");
            
    if(mysqli_connect_error($con)){
        echo "Failed to connect MySQL: " . mysqli_connect_error();
    }

    $create_time=filter_input(INPUT_POST,'create_time');
    $end_time=filter_input(INPUT_POST,'end_time');
    $route_info=filter_input(INPUT_POST,'route_info');
    $route_order=filter_input(INPUT_POST,'route_order');
    $route_Id=filter_input(INPUT_POST,'route_id');
    $UserID=filter_input(INPUT_POST,'UserID');
    $result = mysqli_query($con,"INSERT INTO route VALUES ('$UserID','$create_time','$end_time','$route_info','$route_order','$route_id')");
    
    if($result){
        echo 'success';
    }
    else{
        echo 'failure';
    }
    
    mysqli_close($con);
    
?>