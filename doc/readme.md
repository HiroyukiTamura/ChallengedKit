### ローカルに持つtemplateの初期値  
RecordDataのリストをシリアライズで保存する。その内容は以下の通り。  
実際の内容は、MainActivityのinitFile()メソッドを見てください.

##### RecordDataのメンバ変数.
public int year;  
public int mon;  
public int day;  
public int dataType;  
public String dataName;  
public HashMap<String, Object> data;

##### dataTypeの実装は以下のとおり。
0: ヘッダータグ
1: タイムライン
2: タグプール
3: リスト
4: 自由記述
  
  
### todo List 
##### data.dataはnullでありうる
firebaseのdatabaseでは、値がnullであると自動的にノードも削除されてしまう。
したがって、data.dataは空でありうる以前にnullでありうる。
グローバルなnullチェックが必要なので、今後実装してください。

##### 禁則文字チェック  
Firebaseには、Key, Valueともに受け付けない文字があります。
文字入力の際などに必ずチェックしてください。

##### 予定リスト
userParamsSeriese 
- userId 
    - 201707 : jsonObject  
          ex)  
        {
            22:["ふがふが", "ほげほげ”]
            24:["ふにふに", "ふがふが”]
        }
        
##### Params
data.dataは例えば次のようなものとなる。  
なお、配列0番目の値は、項目のタイプを表す。
  ex)
 - "0" -> CheckBox
 - "1" -> 数量表示（1～5など）。  
 
######なお、配列0番目="2" -> 配列2番目の値は初期値を表し、配列3番目の値は上限値を表す。
 ex)
 - "0": "0", "朝", "true"
 - "1": "0", "頓服", "false" 
 - "2": "1", "気分", "3", "5"