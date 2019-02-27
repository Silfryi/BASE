package com.teamacronymcoders.base.client.models;

import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType;
import net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.client.model.pipeline.*;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;

//Borrowed from IE with Permission
public class SmartLightingQuad extends BakedQuad {
    private static Field parent;
    private static Field blockInfo;

    static {
        try {
            blockInfo = VertexLighterFlat.class.getDeclaredField("blockInfo");
            blockInfo.setAccessible(true);
            parent = QuadGatheringTransformer.class.getDeclaredField("parent");
            parent.setAccessible(true);
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    BlockPos blockPos;
    int[][] relativePos;
    boolean ignoreLight;
    public static int staticBrightness;

    public SmartLightingQuad(int[] vertexDataIn, int tintIndexIn, EnumFacing faceIn, TextureAtlasSprite spriteIn, VertexFormat format, BlockPos p) {
        super(vertexDataIn, tintIndexIn, faceIn, spriteIn, false, format);
        blockPos = p;
        relativePos = new int[4][];
        ignoreLight = false;
        for (int i = 0; i < 4; i++) {
            relativePos[i] = new int[]{(int) Math.floor(Float.intBitsToFloat(vertexDataIn[7 * i])),
                    (int) Math.floor(Float.intBitsToFloat(vertexDataIn[7 * i + 1])),
                    (int) Math.floor(Float.intBitsToFloat(vertexDataIn[7 * i + 2]))
            };
        }
    }

    public SmartLightingQuad(int[] vertexDataIn, int tintIndexIn, EnumFacing faceIn, TextureAtlasSprite spriteIn, VertexFormat format) {
        super(vertexDataIn, tintIndexIn, faceIn, spriteIn, false, format);
        ignoreLight = true;
    }

    @Override
    public void pipe(@Nonnull IVertexConsumer consumer) {
        IWorldReader world = null;
        BlockInfo info;
        if (consumer instanceof VertexLighterFlat) {
            try {
                info = (BlockInfo) blockInfo.get(consumer);
                world = info.getWorld();
                consumer = (IVertexConsumer) parent.get(consumer);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        consumer.setQuadOrientation(this.getFace());
        if (this.hasTintIndex()) {
            consumer.setQuadTint(this.getTintIndex());
        }
        float[] data = new float[4];
        VertexFormat format = consumer.getVertexFormat();
        int count = format.getElementCount();
        int[] eMap = LightUtil.mapFormats(format, DefaultVertexFormats.ITEM);
        int itemCount = DefaultVertexFormats.ITEM.getElementCount();
        eMap[eMap.length - 1] = 2;
        for (int v = 0; v < 4; v++) {
            for (int e = 0; e < count; e++) {
                if (eMap[e] != itemCount) {
                    //lightmap is UV with 2 shorts
                    if (format.getElement(e).getUsage() == EnumUsage.UV && format.getElement(e).getType() == EnumType.SHORT){
                        int brightness;
                        if (!ignoreLight && world != null) {
                            BlockPos here = blockPos.add(relativePos[v][0], relativePos[v][1], relativePos[v][2]);
                            brightness = world.getCombinedLight(here, 0);
                        } else {
                            brightness = staticBrightness;
                        }
                        data[0] = ((float) ((brightness >> 0x04) & 0xF) * 0x20) / 0xFFFF;
                        data[1] = ((float) ((brightness >> 0x14) & 0xF) * 0x20) / 0xFFFF;
                    } else {
                        LightUtil.unpack(this.getVertexData(), data, DefaultVertexFormats.ITEM, v, eMap[e]);
                    }
                    consumer.put(e, data);
                } else {
                    consumer.put(e, 0);
                }
            }
        }
    }
}
