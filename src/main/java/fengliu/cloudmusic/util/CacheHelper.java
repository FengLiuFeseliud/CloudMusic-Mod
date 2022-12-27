package fengliu.cloudmusic.util;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.io.File;

import fengliu.cloudmusic.CloudMusicClient;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

/**
 * 缓存工具对象, 仅在创建时遍历一次缓存目录
 */
public class CacheHelper {
    private Path cachePath;
    private List<String> fileaCacheList = new ArrayList<>();
    private int maxMb;
    private int deleteMb;
    private float useMb;

    /**
     * 文件创建时间排序
     * @param fileList 文件数组
     * @return 文件数组
     */
    public static File[] orderByDate(File[] fileList) {
        Arrays.sort(fileList, new Comparator<File>() {
            public int compare(File f1, File f2) {
                long diff = f1.lastModified() - f2.lastModified();
                if (diff > 0)
                    return 1;
                else if (diff == 0)
                    return 0;
                else
                    return -1;
            }

            public boolean equals(Object obj) {
                return true;
            }

        });

       return fileList;
    }

    /**
     * 字节大小转 mb
     * @param size 字节大小
     * @return mb 大小
     */
    public static float setMbSize(long size){
        return Float.valueOf((new DecimalFormat("#.00")).format(size / 1048576));
    }

    /**
     * 缓存工具对象, 仅在创建时遍历一次缓存目录
     */
    public CacheHelper(){
        this.cachePath = Paths.get(CloudMusicClient.CONFIG.getOrDefault("cache.path", Paths.get(CloudMusicClient.MC_PATH.toString(), "cloud_music_cache").toAbsolutePath().toString()));
        this.maxMb = (int) CloudMusicClient.CONFIG.getOrDefault("cache.maxmb", 512);
        this.deleteMb = (int) CloudMusicClient.CONFIG.getOrDefault("cache.deletemb", 128);
        this.loadCachePath();
    } 
    
    /**
     * 加载缓存目录
     */
    public void loadCachePath(){
        File cacheDir = new File(this.cachePath.toString());
        if(!cacheDir.exists()){
            cacheDir.mkdirs();
        }

        if(!cacheDir.isDirectory()){
            this.cachePath = Paths.get(CloudMusicClient.MC_PATH.toString(), "cloud_music_cache").toAbsolutePath();
            this.loadCachePath();
            return;
        }

        long dirSize = 0;
        for(File file: orderByDate(cacheDir.listFiles())){
            if(file.isDirectory()){
                continue;
            }

            dirSize += file.length();
            fileaCacheList.add(file.getName());
        }

        this.useMb = setMbSize(dirSize);
    }

    /**
     * 删除最旧文件
     */
    protected void deleteOldFile(){
        File oldFile = this.getOldCacheFile();
        if(!oldFile.exists()){
            this.fileaCacheList.remove(0);
            return;
        }

        try {
            long size = oldFile.length();
            if(oldFile.delete()){
                this.useMb -= setMbSize(size);
            }
        } catch (Exception e) {
            this.fileaCacheList.add(oldFile.getName());
            return;
        }
        this.fileaCacheList.remove(0);
    }

    /**
     * 一直删除最旧文件, 至到目标缓存大小
     * @param targetMdSize 目标缓存大小
     */
    public void deleteOldFileToTargetSize(int targetMdSize){
        if(targetMdSize == 0){
            targetMdSize = (int) this.useMb;
        }

        if(!((this.useMb + targetMdSize) > maxMb && this.fileaCacheList.size() != 0)){
            return;
        }

        this.deleteOldFile();
        this.deleteOldFileToTargetSize(targetMdSize);
    }

    private class deleteOldFileThread implements Runnable{
        private final CacheHelper helper;
        private int targetMdSize;

        public deleteOldFileThread(CacheHelper helper, int targetMdSize){
            this.targetMdSize = targetMdSize;
            this.helper = helper;
        }

        @Override
        public void run() {
            this.helper.deleteOldFileToTargetSize(targetMdSize);
        }

    }

    /**
     * 向缓存对象记录文件
     * @param file 文件
     */
    public void addUseSize(File file){
        String name = file.getName();
        if(this.fileaCacheList.contains(name)){
            return;
        }
        this.useMb += setMbSize(file.length());
        this.fileaCacheList.add(name);

        Thread deleteOldFileThread = new Thread(new deleteOldFileThread(this, this.deleteMb));
        deleteOldFileThread.setDaemon(true);
        deleteOldFileThread.setName("CloudMusic deleteOldFile Thread");
        deleteOldFileThread.start();
    }

    /**
     * 获取最旧文件对象
     * @return 最旧文件对象
     */
    public File getOldCacheFile(){
        return new File(this.cachePath.toString(), this.fileaCacheList.get(0));
    }

    /**
     * 获取一个在缓存目录下的新文件对象
     * @param fileName 文件名
     * @return 新文件对象
     */
    public File getWaitCacheFile(String fileName){
        return new File(this.cachePath.toString(), fileName);
    }

    /**
     * 向游戏聊天框打印缓存信息
     * @param source Fabric 命令源
     */
    public void printToChatHud(FabricClientCommandSource source) {
        source.sendFeedback(Text.translatable("cloudmusic.info.config.cache", "§c" + this.useMb, "§c" + this.maxMb, "§c" + this.deleteMb));
        source.sendFeedback(Text.translatable("cloudmusic.info.config.cache.path", "§c" + this.cachePath));
    }
}
