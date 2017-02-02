
<?php
header('Content-Type: application/json');
include_once "Database.php";

$json = file_get_contents("php://input");
$array = json_decode($json, true);
switch ($array['action']) {
  case 'login':
    $result=0;
    if(login($array['mail'],$array['password']))
      if(storeToken($array['mail'],$array['password'],$array['token']))
        $result=1;
    $username=getUsername($array['mail']);
    $arr = array('result' => $result, 'username' => $username);
    echo json_encode($arr);
    break;
  case 'sendMessage':
    $rez = storeMessage($array['to'],$array['from'],$array['type'],$array['data']);
    if($rez){
      $token = getToken($array['to']);
      $id = getMessageId();
      sendMessage($array['from'],$array['data'], $array['type'], $token, $id);
    }
    $arr = array('success' => $rez, 'su' => $array['from'], 'suc' => $array['data'], 'succes' => $token, 'sucess' => $id);
    echo json_encode($arr);
    break;
  case 'download':
    $arr = getMessage($array['message_id']);
    echo json_encode($arr);
    break;
  case 'downloadAll':
    $arr = getAllMessages($array['username']);
    echo'{"ProductsData":'.json_encode($arr).'}';
    break;
  case 'addUser':
    $rez = 0;
    $username=getUsername($array['mail']);
    if(is_string($username) && !empty($username))
      $rez = 1;
    $arr = array('success' => $rez, 'username' => $username);
    echo json_encode($arr);
    break;
  case 'logout':
    storeToken($array['mail'],$array['password'],$array['token']);
    break;
}



 ?>
