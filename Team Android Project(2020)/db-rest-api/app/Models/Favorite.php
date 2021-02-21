<?php

namespace app\Models;

use Exception;
use mysqli_stmt;
use app\Utilities\MySqlHandler;

require_once __DIR__ . '/Model.php';


class Favorite extends Model
{
    protected $table = 'favorite';
    protected $key = 'favorite_id';

    protected $fillable = [
        'store_id',
        'account_id'
    ];

    protected $cols = [
        'store_id' => 'i',
        'account_id' => 'i'
    ];

    function __construct( $attributes = array() ){
        $this->setAttributes( $attributes );
    }

    public function getFavoriteId(){
        return $this->attributes['favorite_id'];
    }

    public function getStoreId(){
        return $this->attributes['store_id'];
    }

    public function getAccountId(){
        return $this->attributes['account_id'];
    }

    public static function scopeByUnique( $account_id, $store_id ){

        $fav = null;

        try{
            $query = "SELECT * FROM `favorite` WHERE account_id = ? AND store_id = ? limit 1";
            $argTypes = "ii";
            $args = [];
            $args[] = &$argTypes;
            $args[] = &$account_id;
            $args[] = &$store_id;
            $row = [];
            $dbHandler = new MySqlHandler();
            if( ! $dbHandler->dbConnected() ){
                throw new Exception('DB connection failed');
            }
            $select = $dbHandler->executePreparedQuery( $query, $args, $row );
            if( $select !== null && $select instanceof mysqli_stmt && $select->num_rows > 0 ){
                $select->fetch();
                $select->close();
                $fav = new Favorite();
                $fav->original = $row;
                $fav->attributes = $row;
            }
            $dbHandler->close();
        }catch (Exception $e){
            unset($dbHandler);
            if( $e->getMessage() == 'DB connection failed' ){
                throw new Exception( $e->getMessage() );
            }
        }

        return $fav;
    }
}