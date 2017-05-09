package rxfamily.db;


import android.content.Context;

import java.security.SecureRandom;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class RealmDB {

    public static Realm getRealm(Context context,String dbName){
        byte[] key = new byte[64];
        new SecureRandom().nextBytes(key);
        Realm.init(context);
        //Migration migration = new Migration();
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name(dbName) //文件名
                .schemaVersion(1) //版本号
                //.migration(migration)//数据库版本迁移（数据库升级，当数据库中某个表添加字段或者删除字段）
                .deleteRealmIfMigrationNeeded()//声明版本冲突时自动删除原数据库(当调用了该方法时，上面的方法将失效)。
                .build();//创建
        return Realm.getInstance(config);
    }
}
