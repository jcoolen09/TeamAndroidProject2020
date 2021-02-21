<?php

namespace app\Models;

require_once __DIR__ . '/Model.php';

use Exception;
use mysqli_stmt;
use app\Utilities\MySqlHandler;

class Store extends Model
{
    protected $table = 'store';
    protected $key = 'store_id';

    protected $fillable = [
        'store_id',
        'store_name',
        'google_id'
    ];

    protected $cols = [
        'store_id' => 'i',
        'google_id' => 's',
        'store_name' => 's'
    ];

    function __construct( $attributes = array() ){
        $this->setAttributes( $attributes );
    }

    public function getStoreName(){
        return $this->attributes['store_name'];
    }

    public function getStoreId(){
        return $this->attributes['store_id'];
    }

    public function getGoogleId(){
        return $this->attributes['google_id'];
    }

    public function setStoreName( $name ){
        $this->attributes['store_name'] = $name;
    }

    public function setGoogleId( $id ){
        $this->attributes['google_id'] = $id;
    }

    //get store by google id
    //throws exception
    public static function scopeByGoogleId( $id ){

        $store = null;

        try{
            $query = "SELECT * FROM `store` WHERE google_id = ? limit 1";
            $argTypes = "s";
            $args = [];
            $args[] = &$argTypes;
            $args[] = &$id;
            $row = [];
            $dbHandler = new MySqlHandler();
            if( ! $dbHandler->dbConnected() ){
                throw new Exception('DB connection failed');
            }
            $select = $dbHandler->executePreparedQuery( $query, $args, $row );
            if( $select !== null && $select instanceof mysqli_stmt && $select->num_rows > 0 ){
                $select->fetch();
                $select->close();
                $store = new Store();
                $store->original = $row;
                $store->attributes = $row;
            }
            $dbHandler->close();
        }catch (Exception $e){
            unset($dbHandler);
            if( $e->getMessage() == 'DB connection failed' ){
                throw new Exception( $e->getMessage() );
            }
        }

        return $store;
    }

    public static function scopeByFavorite( $account_id ){

        $stores = [];

        try{
            $query = "SELECT s.* FROM `store` s JOIN `favorite` f ON f.store_id = s.store_id WHERE f.account_id = ?";
            $argTypes = "i";
            $args = [];
            $args[] = &$argTypes;
            $args[] = &$account_id;
            $row = [];
            $dbHandler = new MySqlHandler();
            if( ! $dbHandler->dbConnected() ){
                throw new Exception('DB connection failed');
            }
            $select = $dbHandler->executePreparedQuery( $query, $args, $row );
            if( $select !== null && $select instanceof mysqli_stmt && $select->num_rows > 0 ){

                while( $select->fetch() ){
                    $store = new Store();
                    $values = $dbHandler->derefrence_array($row);
                    $store->original = $values;
                    $store->attributes = $values;
                    $stores[] = $store;
                }

                $select->close();
            }
            $dbHandler->close();
        }catch (Exception $e){
            unset($dbHandler);
            if( $e->getMessage() == 'DB connection failed' ){
                throw new Exception( $e->getMessage() );
            }
        }

        return $stores;
    }
}