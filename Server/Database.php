<?php
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "MyChat";

function login($mail, $pass){
  global $servername, $username, $password, $dbname;
  $conn = new mysqli($servername, $username, $password, $dbname);
  // Check connection
  if ($conn->connect_error) {
      die("Connection failed: " . $conn->connect_error);
  }

  $sql = "SELECT * FROM user_details where mail = '$mail' and password = '$pass'";
  $result = $conn->query($sql);
  $res = 0;
  if ($result->num_rows > 0) {
    $res = 1;
  }
  else {
    $res = 0;
  }
  $conn->close();
  return $res;
}

function storeToken($mail, $pass, $token){
  global $servername, $username, $password, $dbname;
  $conn = new mysqli($servername, $username, $password, $dbname);
  // Check connection
  if ($conn->connect_error) {
      die("Connection failed: " . $conn->connect_error);
  }

  $sql = "UPDATE user_details set token='$token' where mail = '$mail' and password = '$pass'";
  $result=0;
  if ($conn->query($sql) === TRUE) {
    $result= 1;
  } else {
    $result= 0;
  }
  return $result;
}

  function storeMessage($from, $to, $type, $data){
    global $servername, $username, $password, $dbname;
    $conn = new mysqli($servername, $username, $password, $dbname);
    // Check connection
    if ($conn->connect_error) {
        die("Connection failed: " . $conn->connect_error);
    }

    $sql = "INSERT INTO messages (user_to, user_from, data, type) values ('".$from."','".$to."','".$data."','".$type."')";
    $result=0;
    if ($conn->query($sql) === TRUE) {
      $result= 1;
    } else {
      $result= 0;
    }

  $conn->close();
  return $result;
}

function getMessageId(){
  global $servername, $username, $password, $dbname;
  $conn = new mysqli($servername, $username, $password, $dbname);

  if ($conn->connect_error) {
      die("Connection failed: " . $conn->connect_error);
  }

  $sql = "SELECT id FROM messages order by id desc limit 1";
  $result = $conn->query($sql);
  $row = $result->fetch_assoc();
  $id = $row["id"];
  $conn->close();
  return $id;
}

function getMessage($id){
  global $servername, $username, $password, $dbname;
  $conn = new mysqli($servername, $username, $password, $dbname);

  if ($conn->connect_error) {
      die("Connection failed: " . $conn->connect_error);
  }

  $sql = "SELECT * FROM messages where id=".$id;
  $result = $conn->query($sql);
  $row = $result->fetch_assoc();
  $sql = "delete FROM messages where id=".$id;
  $conn->query($sql);
  $conn->close();
  $arr = array('user_from' => $row["user_from"], 'user_to' => $row["user_to"], 'data' => $row["data"], 'type' => $row["type"]);
  return $arr;
}

function getUsername($mail){
  global $servername, $username, $password, $dbname;
  $conn = new mysqli($servername, $username, $password, $dbname);
  // Check connection
  if ($conn->connect_error) {
      die("Connection failed: " . $conn->connect_error);
  }

  $sql = "SELECT username FROM user_details where mail = '$mail'";
  $result = $conn->query($sql);
  $row = $result->fetch_assoc();
  $username = $row["username"];
  $conn->close();
  return $username;
}

function getToken($user){
  global $servername, $username, $password, $dbname;
  $conn = new mysqli($servername, $username, $password, $dbname);
  // Check connection
  if ($conn->connect_error) {
      die("Connection failed: " . $conn->connect_error);
  }

  $sql = "SELECT token FROM user_details where username = '".$user."'";
  $result = $conn->query($sql);
  $row = $result->fetch_assoc();
  $token = $row["token"];
  $conn->close();
  return $token;
}

function getAllMessages($user){
  global $servername, $username, $password, $dbname;
  $conn = new mysqli($servername, $username, $password, $dbname);
  // Check connection
  if ($conn->connect_error) {
      die("Connection failed: " . $conn->connect_error);
  }
  $sql = "SELECT * FROM messages WHERE user_to='".$user."'";
  $rez = array();
  $result = $conn->query($sql);
  $i=0;
  $last_id=0;
  if ($result->num_rows > 0) {
    while($row = $result->fetch_assoc()){
      $rez_in =  array();
      $rez_in['from'] = $row['user_from'];
      $rez_in['to'] = $row['user_to'];
      $rez_in['type'] = $row['type'];
      $rez_in['data'] = $row['data'];
      $last_id=$row['id'];
      $rez[]=$rez_in;
    }
  }
  $sql = "DELETE FROM messages WHERE  user_to='".$user."' and id<=".$last_id;
  $conn->query($sql);
  $conn->close();
  return $rez;

}

 function sendMessage($from, $data, $type, $to_token, $message_id){
   $url = 'https://fcm.googleapis.com/fcm/send';
   $ch = curl_init($url);

   if($type=="image")
      $data=$type;

   $jsonData = array(
       'notification' => array(
         'title' => $from,
         'body' => $data,
         'sound' => 'default',
         'click_action' => 'android.intent.action.MAIN',
         'tag'=>'1'
       ),
       'to' => $to_token,
       'data' => array(
         'message_id' => $message_id
       )
      );

     $jsonDataEncoded = json_encode($jsonData);
     curl_setopt($ch, CURLOPT_POST, true);
     curl_setopt($ch, CURLOPT_POSTFIELDS, $jsonDataEncoded);
     $headr = array();
     $headr[] = 'Content-type: application/json';
     $headr[] = 'Authorization:key=AIzaSyCRtfxc28wj39R4X_jEWpxtN_eps47Mn5k';
     curl_setopt($ch, CURLOPT_HTTPHEADER,   $headr);
     curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
     $result = curl_exec($ch);
 }




?>
