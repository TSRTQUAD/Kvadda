% This function plots all the obtained results

function plotResults( object, nodes, trajectory)

% Calculate the total trajectory distance
distance = 0;
for ii = 2:length(trajectory)
    distance = distance + lldistkm(trajectory(ii-1,:),trajectory(ii,:));
end

% Plot and calculate the covered area in m^2
earthellipsoid = referenceSphere('earth','m');
earthellipsoidsurfacearea = areaquad(-90,-180,90,180,earthellipsoid);
figure('Name','Optimal trajectory for area coverage','Numbertitle','off')
clf; hold on; area = 0; forbiddenarea = 0;
for ii = 1:length(object.area)
    area = area + areaint(object.area{ii}(:,1),object.area{ii}(:,2))*...
        earthellipsoidsurfacearea;
    h1 = fill(object.area{ii}(:,2),object.area{ii}(:,1),[0.5,0.5,0.5]);
end
for ii = 1:length(object.forbiddenarea)
    forbiddenarea = forbiddenarea+areaint(object.forbiddenarea{ii}(:,1),...
        object.forbiddenarea{ii}(:,2))*earthellipsoidsurfacearea;
    fill(object.forbiddenarea{ii}(:,2),object.forbiddenarea{ii}(:,1),'w');
end
area = area - forbiddenarea;
plot(nodes(:,2),nodes(:,1), '.k');
h2 = plot(trajectory(:,2),trajectory(:,1),'r');
legend([h1 h2],{['Total area search: ' num2str(area) 'm^2'],...
    ['Total trajectory distance: ' num2str(distance*1000) 'm']})
plot_google_map;
hold off