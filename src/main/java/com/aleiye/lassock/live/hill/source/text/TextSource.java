package com.aleiye.lassock.live.hill.source.text;

import com.aleiye.common.exception.AuthWrongException;
import com.aleiye.lassock.api.Course;
import com.aleiye.lassock.api.CourseConst;
import com.aleiye.lassock.api.Intelligence.ShadeState;
import com.aleiye.lassock.live.exception.CourseException;
import com.aleiye.lassock.live.exception.SignException;
import com.aleiye.lassock.live.hill.source.AbstractEventDrivenSource;
import com.aleiye.lassock.live.hill.source.text.cluser.CluserListener;
import com.aleiye.lassock.live.hill.source.text.cluser.CluserState;
import com.aleiye.lassock.live.hill.source.text.cluser.FileCluser;
import com.aleiye.lassock.live.hill.source.text.cluser.TextCluser;
import com.aleiye.lassock.live.model.Mushroom;
import com.aleiye.lassock.util.CloseableUtils;
import com.aleiye.lassock.util.DirectorScannerUtils;
import com.aleiye.lassock.util.MarkUtil;
import com.aleiye.lassock.util.ScrollUtils;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 事件驱动采集图
 *
 * @author ruibing.zhao
 * @version 2.1.1
 * @since 2015年5月22日
 */
public class TextSource extends AbstractEventDrivenSource implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(TextSource.class);

    private CluserSign param;
    // private final AtomicBoolean parsed = new AtomicBoolean(false);

    // 等待执行队列(单元对应Shade)
    private final BlockingQueue<TextCluser> normals = new LinkedBlockingQueue<TextCluser>();

    // 发生错误的Shade
    private final BlockingQueue<TextCluser> errors = new LinkedBlockingQueue<TextCluser>();

    // 等待执行队列(单元对应Shade)
    private final List<TextCluser> idles = Collections.synchronizedList(new LinkedList<TextCluser>());

    // 应急备用通道(用于在关闭时保存唤醒阻塞无处存放的Shade)
    private final List<TextCluser> emergency = Collections.synchronizedList(new LinkedList<TextCluser>());

    // 采集执行器
    private CustomThreadPoolExecutor executor;

    // 空闲检查
    private void checkIdles() {
        synchronized (idles) {
            for (int i = 0; i < idles.size(); i++) {
                TextCluser shade = idles.get(i);
                try {
                    try {
                        shade.open();
                    } catch (IOException e) {
                        errors.put(idles.remove(i));
                        return;
                    }
                    if (shade.getState() != CluserState.END) {
                        normals.put(idles.remove(i));
                    } else {
                        IOUtils.closeQuietly(shade);
                    }
                } catch (InterruptedException e) {
                    emergency.add(shade);
                }
            }
        }
    }

    // 控制时间
    long time = System.currentTimeMillis();

    /**
     * 消防检查线程(当事件停止时作检查)
     * 该线程负责可读队列和空闲队列
     */
    public void checkNomalAndIdies() {
        // 获取Shade
        TextCluser shade = null;
        try {
            // 每隔1分种检查空闲
            long curTime = System.currentTimeMillis();
            if ((curTime - time) > 5000) {
                checkIdles();
                time = System.currentTimeMillis();
            }
            // 获取一个Shade
            shade = normals.poll(200, TimeUnit.MILLISECONDS);
            if (shade != null) {

                // 读取结束,自检
                if (shade.getState() == CluserState.END) {
                    shade.selfCheck();
                }
                // 正常时放入执行队列
                if (shade.getState() == CluserState.NORMAL) {
                    executor.execute(shade);
                }
                // 依旧为读取结束时
                else if (shade.getState() == CluserState.END) {
                    // 60次自检没有可读数据时 移动到空闲集
                    if (shade.getEndConut() > 60) {
                        CloseableUtils.closeQuietly(shade);
                        idles.add(shade);
                    } else {
                        boolean isOffer = normals.offer(shade, 60000, TimeUnit.MILLISECONDS);
                        if (!isOffer) {
                            CloseableUtils.closeQuietly(shade);
                            idles.add(shade);
                        }
                    }
                }
                // 其它当异常Shade处理
                else {
                    errors.put(shade);
                }
            }
        } catch (InterruptedException e) {
            if (shade != null) {
                emergency.add(shade);
            }
        }
    }

    /**
     * 医生线程(事件发生异常时修正或移除)
     * 负责异常对列检查和修复
     */
    public void checkError() {
        TextCluser shade = null;
        try {
            // 从异常队列获取一个Shade
            shade = errors.poll(200, TimeUnit.MILLISECONDS);
            if (shade == null) {
                return;
            }
            // 如果该采cluser 已移除，关闭
            if (shade.getSign().isRemoved()) {
                CloseableUtils.closeQuietly(shade);
                return;
            }
            CluserState sts = shade.getState();
            CloseableUtils.closeQuietly(shade);
            if (sts == CluserState.ERR) {
                CluserSign unit = shade.getSign();
                // 文件验证
                File file = new File(unit.getPath());
                String fileKey = unit.getKey();
                boolean findFile = false;
                // 文件存在
                if (file.exists()) {
                    // fileKey 取得（sign 为INODE,WINDOW为文件绝对路径）
                    FileAttributes fa = new FileAttributes(file);
                    String changedKey = fa.getFileKey();
                    // 如果当前fileKey 和 取得FileKey相同
                    if (fileKey.equals(changedKey)) {
                        // 可读
                        if (file.isFile() && file.canRead()) {
                            sts = CluserState.END;
                        } else {
                            sts = CluserState.REMOVED;
                        }
                    } else {
                        shade.setState(CluserState.REMOVED);
                        findFile = true;
                    }
                } else {
                    findFile = true;
                }
                if (findFile) {
                    // 根据所有Course配置移动路径查找该文件
                    String newPath = sign.getCourse().getString(CourseConst.text.MOVE_PATH);
                    // 移动目录和本目录没找到，重新扫描课程表寻找
                    if (StringUtils.isNotBlank(newPath)) {
                        newPath = FileGeter.getFile(newPath, unit.getKey());
                    } else {
                        FileFinder ff = new FileFinder(new String[]{});
                        List<File> findFiles = ff.getFiles(param.getPath());
                        for (File file1 : findFiles) {
                            String findKey = new FileAttributes(file1).getFileKey();
                            if (fileKey.equals(findKey)) {
                                newPath = file1.getPath();
                                break;
                            }
                        }
                    }
                    // 找到文件
                    if (newPath != null) {
                        File newFile = new File(newPath);
                        if (newFile.isFile() && newFile.canRead()) {
                            BasicFileAttributes bfa1 = FileGeter.getFileAttributes(newFile);
                            // 创建时间一样表时同一文件
                            if (shade.getSign().getCt() == bfa1.creationTime().toMillis()) {
                                unit.setPath(newPath);
                                sts = CluserState.END;
                            }
                            // 创建时间不同,已不是同一文件
                            else {
                                sts = CluserState.REMOVED;
                            }
                        }
                        // 不是文件或不能读
                        else {
                            sts = CluserState.REMOVED;
                        }
                    }
                    // 没有找到文件
                    else {
                        sts = CluserState.REMOVED;
                    }
                }
            }
            if (sts == CluserState.END || sts == CluserState.NORMAL) {
                shade.open();
                normals.put(shade);
                return;
            }
            // 如果是移称状态
            if (sts == CluserState.REMOVED) {
                removeSign(shade.getSign());
            }
        } catch (InterruptedException e1) {
            if (shade != null)
                emergency.add(shade);
        } catch (Exception e) {
            if (shade != null) {
                try {
                    errors.put(shade);
                } catch (InterruptedException e1) {
                    emergency.add(shade);
                }
            }
        }
    }

    Thread checkThread;
    boolean shouldRun = true;

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted() && this.shouldRun) {
            try {
                // 检查新文件
                refresh();
                checkNomalAndIdies();
                checkError();
                Thread.sleep(1000);
            } catch (Exception e) {
                ;
            }
        }
    }

    @Override
    public void doStart() throws Exception {
        // 初始化执行线程
        executor = CustomThreadPoolExecutor.newExecutor();
        checkThread = new Thread(this, "text-" + this.getName());
        checkThread.start();
    }

    List<CluserSign> details;

    /**
     * 刷新
     */
    protected void refresh() {
        try {
            List<CluserSign> unitList = new ArrayList<CluserSign>();
            Course course = this.sign.getCourse();
            List<CluserSign> rfu = details;
            if (rfu == null) {
                rfu = new ArrayList<CluserSign>();
            }
            Set<CluserSign> oldUnits = Sets.newHashSet(rfu.iterator());

            // 扫描课程扫描采集集
            List<CluserSign> units = makeDetails(course);
            // 扫描课程扫描采集集
            Set<CluserSign> newUnits = Sets.newHashSet(units.iterator());

            // 新旧差值
            SetView<CluserSign> addUnits = Sets.difference(newUnits, oldUnits);
            for (CluserSign unit : addUnits) {
                unitList.add(unit);
            }
            details = units;
            // 添加差值Unit
            for (CluserSign unit : unitList) {
                addSign(unit);
            }
            if (this.sign.getIntelligence().getState() == ShadeState.ERROR)
                this.sign.getIntelligence().setState(ShadeState.NORMAL);
        } catch (Exception e) {
            this.sign.getIntelligence().setState(ShadeState.ERROR);
            logger.error(e.getMessage(),e);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e1) {
                ;
            }
        }
    }

    // 合并后课程单元（共享型单元,多课程拥有同一单元时，将共享同一个单元）
    protected final Map<String, CluserSign> signs = Collections.synchronizedMap(new HashMap<String, CluserSign>());
    // 采集子源
    protected final Map<String, TextCluser> shades = Collections.synchronizedMap(new HashMap<String, TextCluser>());

    /**
     * 添加Unit
     *
     * @param t
     */
    protected void addSign(CluserSign t) throws SignException, CourseException {
        String key = t.getKey();
        // 已存在相同单元,关联课程ID
        if (signs.containsKey(key)) {
            ;
        }
        // 不存在，
        else {
            // 创建新Shade
            TextCluser s = null;
            try {
                s = new FileCluser(t, this.normals, this.errors, this.emergency);
                long offset = MarkUtil.getMark(t.getKey());
                s.seek(offset);
                s.setListener(new CluserListener() {
                    @Override
                    public void picked(Mushroom mushroom) throws InterruptedException, AuthWrongException {
                        putMushroom(mushroom);
                    }
                });
                // 打开
                s.open();
                afterAddShade(s);
            } catch (Exception e) {
                handleException(e);
            }
            if (s != null) {
                shades.put(key, s);
                signs.put(key, t);
                logger.info("Add File:" + t.getPath());
            }
        }
    }

    /**
     * 移除Shade
     * 默认间接移除
     *
     * @param t
     */
    protected void removeSign(CluserSign t) {
        String key = t.getKey();
        synchronized (signs) {
            if (signs.containsKey(key)) {
                CluserSign removed = signs.remove(key);
                removed.setRemoved(true);
                removeShade(removed);
                logger.info(this.getClass().getSimpleName() + "/File:" + t.getPath() + " was removed!");
            }
        }
    }

    /**
     * 移除一个File source
     *
     * @param t
     */
    private void removeShade(CluserSign t) {
        TextCluser s = shades.remove(t.getKey());
        if (s != null) {
            CloseableUtils.closeQuietly(s);
        }
        MarkUtil.reMark(t.getKey());
    }

    /**
     * 根据课程扫描采集清单
     */
    protected List<CluserSign> makeDetails(Course course) {
        List<CluserSign> details = new ArrayList<CluserSign>();
        String inputPath = param.getPath();
        if (StringUtils.isBlank(inputPath)) {
            throw new IllegalArgumentException("Path in not be empty!");
        }
        String fileIncludesJson = course.getString(CourseConst.text.FILES_INCLUDES);
        String filesExcludesJson = course.getString(CourseConst.text.FILES_EXCLUDES);
        //使用新的路径过滤,降低使用的难度
        File inputFile = new File(inputPath.trim());
        if (inputFile.exists() && inputFile.isFile()) {
            CluserSign sign = creatSign(inputFile);
            sign.setRegular(course.getString(CourseConst.text.DATA_REGULAR));
            sign.setEncode(course.getString(CourseConst.text.DATA_ENCODE));
            details.add(sign);
        } else {
            FilePathParseInfo filePathParseInfo = DirectorScannerUtils.parseFilePath(inputPath);
            String[] fileList = DirectorScannerUtils.scannerFiles(fileIncludesJson, filesExcludesJson, filePathParseInfo);
            if (fileList != null && fileList.length > 0) {
                for (String filePath : fileList) {
                    CluserSign sign = creatSign(new File(filePathParseInfo.getBasePath(), filePath));
                    sign.setRegular(course.getString(CourseConst.text.DATA_REGULAR));
                    sign.setEncode(course.getString(CourseConst.text.DATA_ENCODE));
                    details.add(sign);
                }
            } else {
                throw new IllegalArgumentException("Input path " + inputPath + " can't find any file!");
            }
        }
        return details;
    }


    CluserSign creatSign(File file) {
        FileAttributes bfa = new FileAttributes(file);
        CluserSign sign = new CluserSign();
        sign.setKey(bfa.getFileKey());
        sign.setCt(bfa.getAttributes().creationTime().toMillis());
        sign.setLmt(bfa.getAttributes().lastModifiedTime().toMillis());
        sign.setPath(file.getPath());
        return sign;
    }

    protected void afterAddShade(TextCluser shade) throws Exception {
        if (shade.getState() == CluserState.NORMAL || shade.getState() == CluserState.END) {
            normals.put(shade);
        } else {
            errors.put(shade);
        }
    }

    protected void handleException(Exception e) throws SignException, CourseException {
        throw new SignException(e.getMessage());
    }

    protected void validateCourse(Course course) throws Exception {
        if (StringUtils.isBlank(course.getString(CourseConst.text.DATA_INPUT_PATH))) {
            throw new CourseException("Date input path no be null!");
        }
    }

    @Override
    protected void doStop() {
        this.shouldRun = false;
        // 关闭巡检
        this.checkThread.interrupt();
        // 关闭正常Shade队列
        Iterator<TextCluser> ite = shades.values().iterator();
        while (ite.hasNext()) {
            TextCluser s = ite.next();
            if (s.isOpen()) {
                CloseableUtils.closeQuietly(s);
            }
        }
        normals.clear();
        errors.clear();
        emergency.clear();
        idles.clear();
    }

    @Override
    protected void doConfigure(Course course) throws Exception {
        param = ScrollUtils.forParam(course, CluserSign.class);
        this.sign.getIntelligence().put("path", param.getPath());
    }
}
