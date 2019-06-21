<?php
    $con=mysqli_connect("localhost","root","1234","pro");
 
    mysqli_set_charset($con,"utf8");
            
    if(mysqli_connect_error($con)){
        echo "Failed to connect MySQL: " . mysqli_connect_error();
    }

    $UserName=filter_input(INPUT_POST,'UserName');
    $NickName=filter_input(INPUT_POST,'NickName');
    $PhoneNum=filter_input(INPUT_POST,'PhoneNum');
    $UserID=filter_input(INPUT_POST,'UserID');
    $Password=filter_input(INPUT_POST,'Password');
    $result = mysqli_query($con,"INSERT INTO user (user_id,password,username,nickname,phonenum) VALUES ('$UserID','$Password','$UserName','$NickName','$PhoneNum')");
    
    if($result){
        echo 'success';
    }
    else{
        echo 'failure';
    }
    
    mysqli_close($con);
    
?>
