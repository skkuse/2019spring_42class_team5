<?php
    $db_user="root";
    $db_pass="1234";
    $db_type="mysql";
    $db_host="localhost";
    $db_name="pro";
    $dsn    ="$db_type:host=$db_host; dbname=$db_name; charset=utf8";
    $UserID = filter_input(INPUT_POST, 'UserID');
    $Password = filter_input(INPUT_POST, 'Password');
        
        
//    $UserID = 'ssss';
//    $Password = 'ssss';
    
    try{
        $pdo= new PDO($dsn, $db_user, $db_pass);
        $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    }catch(PDOException $Exception){
        die('Error: '.$Exception->getMessage());
    }
        
    try{
        $sql = "SELECT * FROM user WHERE user_id='$UserID' and Password='$Password'";
        $stmh = $pdo->prepare($sql);
        $stmh->execute();
        $count = $stmh->rowCount();
            
        if($count<1){
            echo "Failure";
        }else{
            echo "User Found";
        }
    } catch (Exception $ex) {
    }
    
?>
