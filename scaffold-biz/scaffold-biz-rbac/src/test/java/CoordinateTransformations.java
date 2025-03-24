public class CoordinateTransformations {

    private static final double a = 6378137.0; // 地球长半轴 (m)
    private static final double f = 1 / 298.257223563; // 扁率
    private static final double b = a * (1 - f); // 地球短半轴 (m)
    private static final double eSquared = 1 - (b * b) / (a * a); // 第一偏心率平方

    public static double[] llhToEcef(double lat, double lon, double h) {
        double N = a / Math.sqrt(1 - eSquared * Math.pow(Math.sin(Math.toRadians(lat)), 2));
        double x = (N + h) * Math.cos(Math.toRadians(lat)) * Math.cos(Math.toRadians(lon));
        double y = (N + h) * Math.cos(Math.toRadians(lat)) * Math.sin(Math.toRadians(lon));
        double z = ((b * b) / (a * a) * N + h) * Math.sin(Math.toRadians(lat));
        return new double[]{x, y, z};
    }

    public static double[] xyzToEcef(double x, double y, double z, double launchLat, double launchLon, double launchAlt) {
        double[] launchPointEcef = CoordinateTransformations.llhToEcef(launchLat, launchLon, launchAlt);
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

    public static void calculateTrajectory(double launchLat, double launchLon, double launchAlt,
                                           double x, double y, double z, double vx, double vy, double vz) {
        double[] ecefCoords = xyzToEcef(x, y, z, launchLat, launchLon, launchAlt);
        double[] llhCoords = ecefToLlh(ecefCoords[0], ecefCoords[1], ecefCoords[2]);
        double azimuth = computeAzimuthAngle(vx, vy, vz, llhCoords[0], llhCoords[1]);

        System.out.println("Latitude: " + llhCoords[0]);
        System.out.println("Longitude: " + llhCoords[1]);
        System.out.println("Altitude: " + llhCoords[2]);
        System.out.println("Azimuth Angle: " + azimuth);
    }

    public static double computeAzimuthAngle(double vx, double vy, double vz, double lat, double lon) {
        double sinLat = Math.sin(Math.toRadians(lat));
        double cosLat = Math.cos(Math.toRadians(lat));
        double sinLon = Math.sin(Math.toRadians(lon));
        double cosLon = Math.cos(Math.toRadians(lon));

        // 旋转矩阵 R，用于将ECEF坐标系下的速度向量转换到ENU坐标系
        double[][] R = {
                {-sinLat * cosLon, -sinLat * sinLon, cosLat},
                {-sinLon, cosLon, 0},
                {-cosLat * cosLon, -cosLat * sinLon, -sinLat}
        };

        // 将速度向量 [vx, vy, vz] 转换到ENU坐标系
        double[] vEnu = new double[3];
        for (int i = 0; i < 3; i++) {
            vEnu[i] = R[i][0] * vx + R[i][1] * vy + R[i][2] * vz;
        }

        // 计算方位角
        double azimuth = Math.toDegrees(Math.atan2(vEnu[0], vEnu[1]));
        if (azimuth < 0) azimuth += 360; // 确保方位角在0°到360°之间
        return azimuth;
    }
}