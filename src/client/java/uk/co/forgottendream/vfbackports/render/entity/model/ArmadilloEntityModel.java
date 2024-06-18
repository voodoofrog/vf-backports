package uk.co.forgottendream.vfbackports.render.entity.model;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.SinglePartEntityModelWithChildTransform;
import net.minecraft.util.math.MathHelper;
import uk.co.forgottendream.vfbackports.entity.passive.ArmadilloEntity;
import uk.co.forgottendream.vfbackports.render.entity.animation.ArmadilloAnimations;

import static uk.co.forgottendream.vfbackports.entity.passive.ArmadilloEntity.HEAD_YAW;

public class ArmadilloEntityModel extends SinglePartEntityModelWithChildTransform<ArmadilloEntity> {
    private static final float TRANSLATION = 16.02F;
    private static final float MAX_HEAD_PITCH = 25.0F;
    private static final float MIN_HEAD_PITCH = 22.5F;
    private static final float LIMB_ANGLE_SCALE = 16.5F;
    private static final float LIMB_DISTANCE_SCALE = 2.5F;
    private static final String HEAD_CUBE = "head_cube";
    private static final String RIGHT_EAR_CUBE = "right_ear_cube";
    private static final String LEFT_EAR_CUBE = "left_ear_cube";
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart cube;
    private final ModelPart head;
    private final ModelPart tail;

    public ArmadilloEntityModel(ModelPart root) {
        super(0.6F, TRANSLATION);
        this.root = root;
        this.body = root.getChild("body");
        this.rightHindLeg = root.getChild("right_hind_leg");
        this.leftHindLeg = root.getChild("left_hind_leg");
        this.head = this.body.getChild("head");
        this.tail = this.body.getChild("tail");
        this.cube = root.getChild("cube");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();
        ModelPartData body = root.addChild("body", ModelPartBuilder.create().uv(0, 20).cuboid(-4.0F, -7.0F, -10.0F, 8.0F, 8.0F, 12.0F, new Dilation(0.3F)).uv(0, 40).cuboid(-4.0F, -7.0F, -10.0F, 8.0F, 8.0F, 12.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 21.0F, 4.0F));
        body.addChild("tail", ModelPartBuilder.create().uv(44, 53).cuboid(-0.5F, -0.0865F, 0.0933F, 1.0F, 6.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -3.0F, 1.0F, 0.5061F, 0.0F, 0.0F));
        ModelPartData head = body.addChild("head", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -2.0F, -11.0F));
        head.addChild(HEAD_CUBE, ModelPartBuilder.create().uv(43, 15).cuboid(-1.5F, -1.0F, -1.0F, 3.0F, 5.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.3927F, 0.0F, 0.0F));
        ModelPartData right_ear = head.addChild("right_ear", ModelPartBuilder.create(), ModelTransform.pivot(-1.0F, -1.0F, 0.0F));
        right_ear.addChild(RIGHT_EAR_CUBE, ModelPartBuilder.create().uv(43, 10).cuboid(-2.0F, -3.0F, 0.0F, 2.0F, 5.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.5F, 0.0F, -0.6F, 0.1886F, -0.3864F, -0.0718F));
        ModelPartData left_ear = head.addChild("left_ear", ModelPartBuilder.create(), ModelTransform.pivot(1.0F, -2.0F, 0.0F));
        left_ear.addChild(LEFT_EAR_CUBE, ModelPartBuilder.create().uv(47, 10).cuboid(0.0F, -3.0F, 0.0F, 2.0F, 5.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.5F, 1.0F, -0.6F, 0.1886F, 0.3864F, 0.0718F));
        root.addChild("right_hind_leg", ModelPartBuilder.create().uv(51, 31).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.0F, 21.0F, 4.0F));
        root.addChild("left_hind_leg", ModelPartBuilder.create().uv(42, 31).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(2.0F, 21.0F, 4.0F));
        root.addChild("right_front_leg", ModelPartBuilder.create().uv(51, 43).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.0F, 21.0F, -4.0F));
        root.addChild("left_front_leg", ModelPartBuilder.create().uv(42, 43).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(2.0F, 21.0F, -4.0F));
        root.addChild("cube", ModelPartBuilder.create().uv(0, 0).cuboid(-5.0F, -10.0F, -6.0F, 10.0F, 10.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
        return TexturedModelData.of(modelData, 64, 64);
    }

    public ModelPart getPart() {
        return this.root;
    }

    public void setAngles(ArmadilloEntity armadilloEntity, float f, float g, float h, float i, float j) {
        this.getPart().traverse().forEach(ModelPart::resetTransform);
        if (armadilloEntity.isRolledUp()) {
            this.body.hidden = true;
            this.leftHindLeg.visible = false;
            this.rightHindLeg.visible = false;
            this.tail.visible = false;
            this.cube.visible = true;
        } else {
            this.body.hidden = false;
            this.leftHindLeg.visible = true;
            this.rightHindLeg.visible = true;
            this.tail.visible = true;
            this.cube.visible = false;
            this.head.pitch = MathHelper.clamp(j, -MIN_HEAD_PITCH, MAX_HEAD_PITCH) * 0.017453292F;
            this.head.yaw = MathHelper.clamp(i, -HEAD_YAW, HEAD_YAW) * 0.017453292F;
        }

        this.animateMovement(ArmadilloAnimations.IDLE, f, g, LIMB_ANGLE_SCALE, LIMB_DISTANCE_SCALE);
        this.updateAnimation(armadilloEntity.unrollingAnimationState, ArmadilloAnimations.UNROLLING, h, 1.0F);
        this.updateAnimation(armadilloEntity.rollingAnimationState, ArmadilloAnimations.ROLLING, h, 1.0F);
        this.updateAnimation(armadilloEntity.scaredAnimationState, ArmadilloAnimations.SCARED, h, 1.0F);
    }
}
