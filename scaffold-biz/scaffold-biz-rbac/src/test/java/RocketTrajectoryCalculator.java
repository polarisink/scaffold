public class RocketTrajectoryCalculator {

    private static final double a = 6378137.0; // 地球长半轴 (m)
    private static final double f = 1 / 298.257223563; // 扁率
    private static final double b = a * (1 - f); // 地球短半轴 (m)
    private static final double eSquared = 1 - (b * b) / (a * a); // 第一偏心率平方

    public static void main(String[] args) {
        double launchLat = 39.9042; // 发射点纬度
        double launchLon = 116.4074; // 发射点经度
        double launchAlt = 50; // 发射点高度 (m)

        double x = 1000; // 发射系下的X坐标 (m)
        double y = 2000; // 发射系下的Y坐标 (m)
        double z = 3000; // 发射系下的Z坐标 (m)

        double vx = 100; // 发射系下的X方向速度 (m/s)
        double vy = 200; // 发射系下的Y方向速度 (m/s)
        double vz = 300; // 发射系下的Z方向速度 (m/s)

        calculateTrajectory(launchLat, launchLon, launchAlt, x, y, z, vx, vy, vz);
    }

    public static void calculateTrajectory(double launchLat, double launchLon, double launchAlt,
                                           double x, double y, double z, double vx, double vy, double vz) {
        double[] ecefCoords = xyzToEcef(x, y, z, launchLat, launchLon, launchAlt);
        double[] llhCoords = ecefToLlh(ecefCoords[0], ecefCoords[1], ecefCoords[2]);
        double azimuth = computeAzimuthAngle(vx, vy, vz, llhCoords[0], llhCoords[1]);
        double pitch = computePitchAngle(vx, vy, vz, llhCoords[0], llhCoords[1]);

        System.out.println("纬度 : " + llhCoords[0]);
        System.out.println("经度: " + llhCoords[1]);
        System.out.println("高度: " + llhCoords[2]);
        System.out.println("方位角: " + azimuth);
        System.out.println("俯仰角: " + pitch);
    }

    public static double[] llhToEcef(double lat, double lon, double h) {
        double N = a / Math.sqrt(1 - eSquared * Math.pow(Math.sin(Math.toRadians(lat)), 2));
        double x = (N + h) * Math.cos(Math.toRadians(lat)) * Math.cos(Math.toRadians(lon));
        double y = (N + h) * Math.cos(Math.toRadians(lat)) * Math.sin(Math.toRadians(lon));
        double z = ((b * b) / (a * a) * N + h) * Math.sin(Math.toRadians(lat));
        return new double[]{x, y, z};
    }

    public static double[] xyzToEcef(double x, double y, double z, double launchLat, double launchLon, double launchAlt) {
        double[] launchPointEcef = llhToEcef(launchLat, launchLon, launchAlt);
        double[] ecefCoords = new double[3];
        ecefCoords[0] = launchPointEcef[0] + x;
        ecefCoords[1] = launchPointEcef[1] + y;
        ecefCoords[2] = launchPointEcef[2] + z;
        return ecefCoords;
    }

    public static double[] ecefToLlh(double x, double y, double z) {
        double p = Math.sqrt(x * x + y * y);
        double theta = Math.atan2(z * a, p * b);

        double lat = Math.atan2(z + eSquared * b * Math.pow(Math.sin(theta), 3),
                p - eSquared * a * Math.pow(Math.cos(theta), 3));
        double lon = Math.atan2(y, x);
        double N = a / Math.sqrt(1 - eSquared * Math.pow(Math.sin(lat), 2));
        double h = p / Math.cos(lat) - N;

        return new double[]{Math.toDegrees(lat), Math.toDegrees(lon), h};
    }

    public static double computeAzimuthAngle(double vx, double vy, double vz, double lat, double lon) {
        double sinLat = Math.sin(Math.toRadians(lat));
        double cosLat = Math.cos(Math.toRadians(lat));
        double sinLon = Math.sin(Math.toRadians(lon));
        double cosLon = Math.cos(Math.toRadians(lon));

        double[][] R = {
                {-sinLat * cosLon, -sinLat * sinLon, cosLat},
                {-sinLon, cosLon, 0},
                {-cosLat * cosLon, -cosLat * sinLon, -sinLat}
        };

        double[] vEnu = new double[3];
        for (int i = 0; i < 3; i++) {
            vEnu[i] = R[i][0] * vx + R[i][1] * vy + R[i][2] * vz;
        }

        double azimuth = Math.toDegrees(Math.atan2(vEnu[0], vEnu[1]));
        if (azimuth < 0) azimuth += 360;
        return azimuth;
    }

    public static double computePitchAngle(double vx, double vy, double vz, double lat, double lon) {
        double sinLat = Math.sin(Math.toRadians(lat));
        double cosLat = Math.cos(Math.toRadians(lat));
        double sinLon = Math.sin(Math.toRadians(lon));
        double cosLon = Math.cos(Math.toRadians(lon));

        double[][] R = {
                {-sinLat * cosLon, -sinLat * sinLon, cosLat},
                {-sinLon, cosLon, 0},
                {-cosLat * cosLon, -cosLat * sinLon, -sinLat}
        };

        double[] vEnu = new double[3];
        for (int i = 0; i < 3; i++) {
            vEnu[i] = R[i][0] * vx + R[i][1] * vy + R[i][2] * vz;
        }

        double pitch = Math.toDegrees(Math.atan2(vEnu[2], Math.sqrt(vEnu[0] * vEnu[0] + vEnu[1] * vEnu[1])));
        return pitch;
    }
}