# CameraTimer
A Camera Library with timer

### Usage

#### App Manifest
```xml
<activity android:name="com.svk.cameratimerlib.activity.CameraActivity"
          android:theme="@style/FullScreenLightTheme"
          android:screenOrientation="portrait"/>
```

#### Activity

```java
CameraTimer.with(this)
                .timeLife(/*TimeSeconds*/)
                .requireImageCount(/*Integer Value*/)
                .startCameraActivity(/*Random Integer*/);
```
Implement onActivityResult
 
```java
  @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQ_CODE){
            if(resultCode == Activity.RESULT_OK){
                ArrayList<String> list = (ArrayList<String>)data.getSerializableExtra(CameraActivity.KEY_DATA);

                for (int i = 0; i < list.size() ; i++) {
                    Log.d(TAG, "onActivityResult : poisition "+ (i+1) + " : "+list.get(i));
                }
                Toast.makeText(this, "Ok", Toast.LENGTH_SHORT).show();

            }else if(resultCode == Activity.RESULT_CANCELED){
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }
 ```
