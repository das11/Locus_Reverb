//package kdas.i_nterface.locusreverb;
//
//import android.os.Parcel;
//
//import com.google.android.gms.common.internal.ReflectedParcelable;
//import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
//import com.google.android.gms.maps.model.zze;
//import com.google.android.gms.maps.model.zza;
//
///**
// * Created by Interface on 29/10/16.
// */
//
//public class LatLngPolyPOJO extends AbstractSafeParcelable implements ReflectedParcelable {
//    public static final zze CREATOR = new zze();
//    private final int mVersionCode;
//    public final double latitude;
//    public final double longitude;
//
//    LatLngPolyPOJO(int var1, double var2, double var4) {
//        this.mVersionCode = var1;
//        if (-180.0D <= var4 && var4 < 180.0D) {
//            this.longitude = var4;
//        } else {
//            this.longitude = ((var4 - 180.0D) % 360.0D + 360.0D) % 360.0D - 180.0D;
//        }
//
//        this.latitude = Math.max(-90.0D, Math.min(90.0D, var2));
//    }
//
//    public LatLngPolyPOJO(double var1, double var3) {
//        this(1, var1, var3);
//    }
//
//    int getVersionCode() {
//        return this.mVersionCode;
//    }
//
//    public void writeToParcel(Parcel var1, int var2) {
//        zze.zza(this, var1, var2);
//    }
//
//    public int hashCode() {
//        boolean var1 = true;
//        boolean var2 = true;
//        long var3 = Double.doubleToLongBits(this.latitude);
//        boolean var10000 = true;
//        boolean var10001 = true;
//        int var5 = 31 + (int) (var3 ^ var3 >>> 32);
//        var3 = Double.doubleToLongBits(this.longitude);
//        var5 = 31 * var5 + (int) (var3 ^ var3 >>> 32);
//        return var5;
//    }
//
//    public boolean equals(Object var1) {
//        if (this == var1) {
//            return true;
//        } else if (!(var1 instanceof com.google.android.gms.maps.model.LatLng)) {
//            return false;
//        } else {
//            com.google.android.gms.maps.model.LatLng var2 = (com.google.android.gms.maps.model.LatLng) var1;
//            return Double.doubleToLongBits(this.latitude) == Double.doubleToLongBits(var2.latitude) && Double.doubleToLongBits(this.longitude) == Double.doubleToLongBits(var2.longitude);
//        }
//    }
//
//    public String toString() {
//        double var1 = this.latitude;
//        double var3 = this.longitude;
//        return (new StringBuilder(60)).append("lat/lng: (").append(var1).append(",").append(var3).append(")").toString();
//    }
//}
