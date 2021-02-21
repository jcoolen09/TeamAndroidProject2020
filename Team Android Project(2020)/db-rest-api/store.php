<?php

$action = $subpage2;

use app\Models\Store;
use app\Models\CheckIn;
use app\Models\Favorite;

switch( $action ){
    case '': getStore(); break;
    case 'add': createStore(); break;
    case 'list': listStores(); break;
    case 'checkin': checkin(); break;
    case 'add-favorite': addFavoriteStore(); break;
    case 'checkins': getCheckins(); break;
    default: header("Location: /404.php");
}

function getStore(){
    $result = ['result' => 'failed', 'error' => ''];
    $store_id = (isset($_REQUEST['store_id']) ? $_REQUEST['store_id'] : null);
    $google_id = (isset($_REQUEST['google_id']) ? $_REQUEST['google_id'] : null);

    try{

        if( empty($store_id) && empty($google_id)  ){
            throw new Exception('Id missing.');
        }

        if( !empty($store_id) ){
            $store = Store::find($store_id);
        }else{
            $store = Store::scopeByGoogleId($google_id);
        }

        if( is_null($store) ){
            throw new Exception('store not found');
        }else {
            $result['data'] = [$store];
            $result['result'] = 'success';
        }
    }catch (Exception $e){
        $result['exception'] = $e->getMessage();
    }

    echo json_encode($result);
}

function listStores(){
    try{

        $stores = Store::all();

        $result['data'] = $stores;
        $result['result'] = 'success';

    }catch (Exception $e){
        $result['exception'] = $e->getMessage();
    }

    echo json_encode($result);
}

function checkin(){
    $result = ['result' => 'failed', 'error' => ''];
    $google_id = (isset($_REQUEST['google_id']) ? $_REQUEST['google_id'] : null);
    $store_name = (isset($_REQUEST['name']) ? $_REQUEST['name'] : null);
    $account_id = (isset($_REQUEST['account_id']) ? $_REQUEST['account_id'] : null);

    $attr = [
        'store_id' => '',
        'account_id' => $account_id
    ];

    try{
        if( is_null($google_id) ){
            throw new Exception('Missing place id');
        }

        $store = Store::scopeByGoogleId($google_id);
        if( is_null($store) ){
            $store = createStore(false);
        }
        $attr['store_id'] = $store->getStoreId();

        $current_checkin = CheckIn::scopeByAccountStoreTime($account_id, $store->getStoreId());
        if( !empty($current_checkin) ){
            $result['result'] = 'success';
            $result['data'] = [$store];
        }else {

            $checkin = new CheckIn($attr);
            if ($checkin->insert()) {
                $result['result'] = 'success';
                $result['data'] = [$store];
            } else {
                throw new Exception('could not insert into db');
            }
        }
    }catch (Exception $e){
        $result['exception'] = $e->getMessage();
    }

    echo json_encode($result);
}

function addFavoriteStore(){
    $result = ['result' => 'failed', 'error' => ''];
    $account_id = (isset($_REQUEST['account_id']) ? $_REQUEST['account_id'] : null);
    $google_id = (isset($_REQUEST['google_id']) ? $_REQUEST['google_id'] : null);

    $attr = [
        'account_id' => $account_id,
        'store_id' => ''
    ];

    try{
        if( is_null($google_id) ){
            throw new Exception('Missing place id');
        }

        $store = Store::scopeByGoogleId($google_id);
        if( is_null($store) ){
            $store = createStore(false);
        }
        $attr['store_id'] = $store->getStoreId();

        $fav = Favorite::scopeByUnique($account_id, $store->getStoreId());
        if( !empty($fav) ){
            $result['result'] = 'success';
            $result['data'] = [$store];
        }else {

            $fav = new Favorite($attr);
            if ($fav->insert()) {
                $result['result'] = 'success';
                $result['data'] = [$store];
            } else {
                throw new Exception('could not insert into db');
            }
        }
    }catch (Exception $e){
        $result['exception'] = $e->getMessage();
    }

    echo json_encode($result);
}

function createStore( $redirect = true ){
    $result = ['result' => 'failed', 'error' => ''];
    $store_name = (isset($_REQUEST['name']) ? $_REQUEST['name'] : null);
    $google_id = (isset($_REQUEST['google_id']) ? $_REQUEST['google_id'] : null);

    $attr = [
        'store_name' => $store_name,
        'google_id' => $google_id
    ];

    try{
        $store = new Store($attr);
        if( $store->insert() ){
            $result['result'] = 'success';
            $result['data'] = [$store];
        }else{
            throw new Exception('could not insert into db');
        }
    }catch (Exception $e){
        $result['exception'] = $e->getMessage();
        if( !$redirect ){
            throw new Exception($e->getMessage());
        }
    }

    if( $redirect ) {
        echo json_encode($result);
    }else{
        return $store;
    }
}

function getCheckins(){
    $result = ['result' => 'failed', 'error' => ''];
    $store_id = (isset($_REQUEST['store_id']) ? $_REQUEST['store_id'] : null);

    try{

        if( is_null($store_id) || empty($store_id) ){
            throw new Exception('Store id missing.');
        }

        $items = CheckIn::scopeByStoreId($store_id);
        $result['data'] = $items;
        $result['result'] = 'success';
    }catch (Exception $e){
        $result['exception'] = $e->getMessage();
    }

    echo json_encode($result);
}