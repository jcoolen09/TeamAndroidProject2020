<?php

$action = $subpage2;

use app\Models\Account;
use app\Models\CheckIn;
use app\Models\Balance;
use app\Models\Store;

switch( $action ){
    case '': getAccount(); break;
    case 'checkins': getCheckins(); break;
    case 'balance': getBalance(); break;
    case 'favorite-eateries': getFavoriteStores(); break;
    case 'set-balance': setBalance(); break;
    default: header("Location: /404.php");
}

//handle list items request
function getAccount(){
    $result = ['result' => 'failed', 'error' => ''];
    $email = (isset($_REQUEST['email']) ? $_REQUEST['email'] : null);

    try{

        if( is_null($email) || empty($email) ){
            throw new Exception('Email missing.');
        }

        $account = Account::scopeByEmail($email);
        if( is_null($account) ){
           $result = createAccount($email);
        }else {
            $result['data'] = [$account];
            $result['result'] = 'success';
        }
    }catch (Exception $e){
        $result['exception'] = $e->getMessage();
    }

    echo json_encode($result);
}

function createAccount($email){
    $result = ['result' => 'failed', 'error' => ''];

    $attr = [
        'email' => $email
    ];

    try{
        $account = new Account($attr);
        if( $account->insert() ){
            $result['result'] = 'success';
            $result['data'] = [$account];
        }else{
            throw new Exception('could not insert into db');
        }
    }catch (Exception $e){
        $result['exception'] = $e->getMessage();
    }

    return $result;
}

function getCheckins(){
    $result = ['result' => 'failed', 'error' => ''];
    $account_id = (isset($_REQUEST['account_id']) ? $_REQUEST['account_id'] : null);

    try{

        if( is_null($account_id) || empty($account_id) ){
            throw new Exception('Account id missing.');
        }

        $items = CheckIn::scopeByAccountId($account_id);
        $result['data'] = $items;
        $result['result'] = 'success';
    }catch (Exception $e){
        $result['exception'] = $e->getMessage();
    }

    echo json_encode($result);
}

function getFavoriteStores(){
    $result = ['result' => 'failed', 'error' => ''];
    $account_id = (isset($_REQUEST['account_id']) ? $_REQUEST['account_id'] : null);

    try{

        if( is_null($account_id) || empty($account_id) ){
            throw new Exception('Account id missing.');
        }

        $items = Store::scopeByFavorite($account_id);
        $result['data'] = $items;
        $result['result'] = 'success';
    }catch (Exception $e){
        $result['exception'] = $e->getMessage();
    }

    echo json_encode($result);
}

function getBalance(){
    $result = ['result' => 'failed', 'error' => ''];
    $account_id = (isset($_REQUEST['account_id']) ? $_REQUEST['account_id'] : null);

    try{

        if( is_null($account_id) || empty($account_id) ){
            throw new Exception('Account id missing.');
        }

        $balance = Balance::scopeByAccountId($account_id);
        if( is_null($balance) ){
            $result = createBalance($account_id);
        }else {
            $result['data'] = [$balance];
            $result['result'] = 'success';
        }
    }catch (Exception $e){
        $result['exception'] = $e->getMessage();
    }

    echo json_encode($result);
}

function setBalance(){
    $result = ['result' => 'failed', 'error' => ''];
    $account_id = (isset($_REQUEST['account_id']) ? $_REQUEST['account_id'] : null);
    $amount = (isset($_REQUEST['balance']) ? $_REQUEST['balance'] : null);

    try{

        if( is_null($account_id) || empty($account_id) ){
            throw new Exception('Account id missing.');
        }

        $balance = Balance::scopeByAccountId($account_id);
        if( is_null($balance) ){
            $result = createBalance($account_id, $amount);
        }else {
            $balance->setBalance($amount);
            if( $balance->save() ){
                $result['result'] = 'success';
                $result['data'] = [$balance];
            }else{
                $result['error'] = 'Could not save changes.';
                throw new Exception('Could not save changes.');
            }
        }
    }catch (Exception $e){
        $result['exception'] = $e->getMessage();
    }

    echo json_encode($result);
}

function createBalance($account_id, $balance = 0.00){
    $result = ['result' => 'failed', 'error' => ''];

    $attr = [
        'account_id' => $account_id,
        'balance' => $balance
    ];

    try{
        $balance = new Balance($attr);
        if( $balance->insert() ){
            $result['result'] = 'success';
            $result['data'] = [$balance];
        }else{
            throw new Exception('could not insert into db');
        }
    }catch (Exception $e){
        $result['exception'] = $e->getMessage();
    }

    return $result;
}


?>